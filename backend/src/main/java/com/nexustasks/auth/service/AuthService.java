package com.nexustasks.auth.service;

import com.nexustasks.auth.config.OtpProperties;
import com.nexustasks.auth.dto.*;
import com.nexustasks.auth.entity.RefreshToken;
import com.nexustasks.auth.entity.VerificationCode;
import com.nexustasks.auth.repository.RefreshTokenRepository;
import com.nexustasks.auth.repository.VerificationCodeRepository;
import com.nexustasks.notification.service.EmailService;
import com.nexustasks.security.service.JwtService;
import com.nexustasks.user.entity.Role;
import com.nexustasks.user.entity.User;
import com.nexustasks.user.repository.UserRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No active verification code"));

        if (code.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "Verification code expired");
        }

        if (code.getAttempts() >= otpProperties.maxAttempts()) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many attempts, request a new code");
        }

        String submittedHash = sha256(request.code());
        if (!submittedHash.equals(code.getCodeHash())) {
            code.setAttempts(code.getAttempts() + 1);
            verificationCodeRepository.save(code);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid verification code");
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already verified");
        }

        // Cooldown de renvoi
        verificationCodeRepository
                .findFirstByUserIdAndUsedFalseOrderByCreatedAtDesc(user.getId())
                .ifPresent(code -> {
                    Instant cooldownEnd = code.getCreatedAt().plusSeconds(otpProperties.resendCooldownSeconds());
                    if (Instant.now().isBefore(cooldownEnd)) {
                        throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Please wait before requesting a new code");
                    }
                });

        createAndSendVerificationCode(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        // 1. Authentifier l'utilisateur (lance une exception si mauvais pwd)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().toLowerCase(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("User authenticated but not found"));

        if (!user.isEmailVerified()) {
            throw new IllegalStateException("Email not verified"); // À mapper vers 403 plus tard
        }

        // 2. Générer l'Access Token
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().name());
        String accessToken = jwtService.generateToken(extraClaims, (org.springframework.security.core.userdetails.User) authentication.getPrincipal());

        // 3. Générer et sauvegarder le Refresh Token
        String refreshTokenString = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenString)
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
                .build();
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(
                accessToken,
                refreshTokenString,
                user.getPublicId(),
                user.getRole().name()
        );
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        String tokenValue = request.refreshToken();
        if (tokenValue == null || tokenValue.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing refresh token");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        // 1) Détection de vol : si le token a déjà été "tourné", c'est suspect
        if (refreshToken.getReplacedBy() != null) {
            log.warn("SECURITY: Reuse of rotated refresh token detected for user {}. Revoking all tokens.",
                    refreshToken.getUser().getEmail());
            refreshTokenRepository.deleteByUserId(refreshToken.getUser().getId());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session compromised, all tokens revoked");
        }

        // 2) Vérification expiration
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
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