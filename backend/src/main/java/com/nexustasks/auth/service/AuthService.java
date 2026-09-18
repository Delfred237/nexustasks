package com.nexustasks.auth.service;

import com.nexustasks.audit.entity.AuditAction;
import com.nexustasks.audit.event.AuditEvent;
import com.nexustasks.auth.config.OtpProperties;
import com.nexustasks.auth.dto.*;
import com.nexustasks.auth.entity.RefreshToken;
import com.nexustasks.auth.entity.VerificationCode;
import com.nexustasks.auth.repository.RefreshTokenRepository;
import com.nexustasks.auth.repository.VerificationCodeRepository;
import com.nexustasks.common.exception.BusinessException;
import com.nexustasks.common.exception.ErrorCode;
import com.nexustasks.notification.service.EmailService;
import com.nexustasks.security.service.JwtService;
import com.nexustasks.user.entity.Role;
import com.nexustasks.user.entity.User;
import com.nexustasks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final OtpProperties otpProperties;
    private final ApplicationEventPublisher eventPublisher;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;


    // =====================================================
    // REGISTER
    // =====================================================
    @Transactional
    public UserResponse register(RegisterRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .enabled(false)
                .emailVerified(false)
                .build();
        user = userRepository.save(user);

        createAndSendVerificationCode(user);

        return UserResponse.from(user);
    }

    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        User user = findUserByEmail(request.email());

        // Idempotence : si déjà vérifié, on ne fait rien
        if (user.isEmailVerified()) {
            return;
        }

        VerificationCode code = verificationCodeRepository
                .findFirstByUserIdAndUsedFalseOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.OTP_ACTIVE_NOT_FOUND));

        if (code.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException(ErrorCode.OTP_EXPIRED);
        }

        if (code.getAttempts() >= otpProperties.maxAttempts()) {
            throw new BusinessException(ErrorCode.OTP_MAX_ATTEMPTS);
        }

        String submittedHash = sha256(request.code());
        if (!submittedHash.equals(code.getCodeHash())) {
            code.setAttempts(code.getAttempts() + 1);
            verificationCodeRepository.save(code);
            throw new BusinessException(ErrorCode.INVALID_OTP);
        }

        code.setUsed(true);
        verificationCodeRepository.save(code);

        // Activer le compte utilisateur
        user.setEmailVerified(true);
        user.setEnabled(true);
        userRepository.save(user);

        // Log optionnel pour le debugging
        log.info("User {} successfully verified their email", user.getEmail());
    }

    @Transactional
    public void resendVerification(ResendVerificationRequest request) {
        User user = findUserByEmail(request.email());

        if (user.isEmailVerified()) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        // Cooldown de renvoi
        verificationCodeRepository
                .findFirstByUserIdAndUsedFalseOrderByCreatedAtDesc(user.getId())
                .ifPresent(code -> {
                    Instant cooldownEnd = code.getCreatedAt().plusSeconds(otpProperties.resendCooldownSeconds());
                    if (Instant.now().isBefore(cooldownEnd)) {
                        throw new BusinessException(ErrorCode.OTP_MAX_ATTEMPTS);
                    }
                });

        createAndSendVerificationCode(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        // 1. Vérifier d'abord si l'utilisateur existe et son état
        User user = userRepository.findByEmail(normalizedEmail).orElse(null);

        if (user != null && !user.isEmailVerified()) {
            // On révèle volontairement que le compte n'est pas vérifié
            // pour améliorer l'UX (l'utilisateur sait qu'il doit vérifier son email)
            throw new BusinessException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        // 2. Tenter l'authentification Spring Security
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(normalizedEmail, request.password())
            );
        } catch (BadCredentialsException e) {
            // Message générique pour ne pas révéler si l'email existe ou non
            throw new BusinessException(
                    ErrorCode.INVALID_CREDENTIALS
            );
        } catch (DisabledException e) {
            throw new BusinessException(
                    ErrorCode.ACCOUNT_DISABLED
            );
        } catch (LockedException e) {
            throw new BusinessException(
                    ErrorCode.ACCOUNT_LOCKED
            );
        }

        // 3. À ce stade, l'utilisateur est authentifié ET vérifié
        if (user == null) {
            user = userRepository.findByEmail(normalizedEmail)
                    .orElseThrow(() -> new IllegalStateException("User authenticated but not found"));
        }

        // 4. Générer les tokens
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().name());
        String accessToken = jwtService.generateToken(extraClaims,
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal());

        String refreshTokenString = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenString)
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
                .build();
        refreshTokenRepository.save(refreshToken);

        // Publier l'événement d'audit
        eventPublisher.publishEvent(new AuditEvent(
                this,
                AuditAction.USER_LOGIN,
                user.getPublicId(),
                user.getEmail(),
                "USER",
                user.getPublicId(),
                null, // IP sera capturée au niveau du controller
                null
        ));

        return new AuthResponse(
                accessToken,
                refreshTokenString,
                user.getPublicId(),
                user.getRole().name());
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        String tokenValue = request.refreshToken();
        if (tokenValue == null || tokenValue.isBlank()) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

        // 1) Détection de vol : si le token a déjà été "tourné", c'est suspect
        if (refreshToken.getReplacedBy() != null) {
            log.warn("SECURITY: Reuse of rotated refresh token detected for user {}. Revoking all tokens.",
                    refreshToken.getUser().getEmail());
            refreshTokenRepository.deleteByUserId(refreshToken.getUser().getId());
            throw new BusinessException(ErrorCode.TOKEN_REVOKED);
        }

        // 2) Vérification expiration
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        User user = refreshToken.getUser();

        // 3) Marquer l'ancien token comme remplacé (rotation)
        String newRefreshTokenValue = UUID.randomUUID().toString();
        refreshToken.setReplacedBy(newRefreshTokenValue);
        refreshTokenRepository.save(refreshToken);

        // 4) Créer le nouveau refresh token
        RefreshToken newRefreshToken = RefreshToken.builder()
                .user(user)
                .token(newRefreshTokenValue)
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
                .build();
        refreshTokenRepository.save(newRefreshToken);

        // 5) Générer un nouvel access token
        var userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().name());
        String newAccessToken = jwtService.generateToken(extraClaims, userDetails);

        return new AuthResponse(
                newAccessToken,
                newRefreshTokenValue,
                user.getPublicId(),
                user.getRole().name()
        );
    }

    @Transactional
    public void logout(User user) {
        // Révoque TOUS les refresh tokens de l'utilisateur
        refreshTokenRepository.deleteByUserId(user.getId());
        log.info("User {} logged out, all refresh tokens revoked", user.getEmail());
    }


    // =====================================================
    // PRIVATE HELPERS
    // =====================================================
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private void createAndSendVerificationCode(User user) {
        // Invalider les anciens codes
        verificationCodeRepository.deleteByUserId(user.getId());

        String rawCode = generateOtp();
        VerificationCode vc = VerificationCode.builder()
                .user(user)
                .codeHash(sha256(rawCode))
                .expiresAt(Instant.now().plusSeconds(otpProperties.ttlMinutes() * 60L))
                .attempts(0)
                .used(false)
                .build();
        verificationCodeRepository.save(vc);

        emailService.sendVerificationEmail(user.getEmail(), user.getFirstName(), rawCode);
    }

    private String generateOtp() {
        // Génère un code à 6 chiffres entre 100000 et 999999
        int code = SECURE_RANDOM.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 est obligatoire dans toute JVM conforme, donc ce cas ne devrait jamais arriver
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}