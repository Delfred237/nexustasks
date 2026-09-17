package com.nexustasks.auth.service;

import com.nexustasks.auth.dto.AuthResponse;
import com.nexustasks.auth.dto.LoginRequest;
import com.nexustasks.auth.entity.RefreshToken;
import com.nexustasks.auth.repository.RefreshTokenRepository;
import com.nexustasks.security.service.JwtService;
import com.nexustasks.user.entity.User;
import com.nexustasks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        // 1. Authentifier l'utilisateur (lance une exception si mauvais pwd)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
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
}