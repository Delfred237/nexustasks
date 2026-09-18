package com.nexustasks.auth.controller;

import com.nexustasks.auth.dto.*;
import com.nexustasks.auth.service.AuthCookieService;
import com.nexustasks.auth.service.AuthService;
import com.nexustasks.security.service.SecurityUserService;
import com.nexustasks.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints publics d'authentification")
public class AuthController {

    private final AuthService authService;
    private final AuthCookieService authCookieService;
    private final SecurityUserService securityUserService;


    @PostMapping("/register")
    @Operation(
            summary = "Inscription d'un nouvel utilisateur",
            description = "Crée un compte avec email non vérifié et envoie un code OTP par email."
    )
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/verify-email")
    @Operation(
            summary = "Vérification de l'email",
            description = "Valide le code OTP reçu par email et active le compte."
    )
    public ResponseEntity<Void> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        authService.verifyEmail(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend-verification")
    @Operation(
            summary = "Renvoyer le code de vérification",
            description = "Génère et envoie un nouveau code OTP (cooldown de 60 secondes)."
    )
    public ResponseEntity<Void> resendVerification(@Valid @RequestBody ResendVerificationRequest request) {
        authService.resendVerification(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    @Operation(
            summary = "Authentification",
            description = "Authentifie l'utilisateur et retourne les tokens (dans le body ET en cookies HttpOnly)."
    )
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        AuthResponse auth = authService.login(request);
        authCookieService.addAccessTokenCookie(response, auth.accessToken());
        authCookieService.addRefreshTokenCookie(response, auth.refreshToken());
        return ResponseEntity.ok(auth);
    }

    /**
     * Refresh : Web lit le refresh token dans le cookie.
     * Mobile l'envoie dans le body JSON.
     */
    @PostMapping("/refresh")
    @Operation(
            summary = "Rafraîchir l'access token",
            description = "Émet un nouvel access token (et refresh token avec rotation). Supporte cookie OU body JSON."
    )
    public ResponseEntity<AuthResponse> refresh(
            @RequestBody(required = false) RefreshRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        // Priorité au cookie (Web), sinon body (Mobile)
        String tokenFromCookie = authCookieService.extractRefreshTokenFromCookie(httpRequest);
        String token = (tokenFromCookie != null && !tokenFromCookie.isBlank())
                ? tokenFromCookie
                : (request != null ? request.refreshToken() : null);

        AuthResponse auth = authService.refresh(new RefreshRequest(token));
        authCookieService.addAccessTokenCookie(httpResponse, auth.accessToken());
        authCookieService.addRefreshTokenCookie(httpResponse, auth.refreshToken());
        return ResponseEntity.ok(auth);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Déconnexion",
            description = "Révoque tous les refresh tokens et supprime les cookies d'authentification."
    )
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        User currentUser = securityUserService.getCurrentUser();
        authService.logout(currentUser);
        authCookieService.clearAuthCookies(response);
        return ResponseEntity.noContent().build();
    }
}