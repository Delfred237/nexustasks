package com.nexustasks.auth.controller;

import com.nexustasks.auth.dto.*;
import com.nexustasks.auth.service.AuthCookieService;
import com.nexustasks.auth.service.AuthService;
import com.nexustasks.security.service.SecurityUserService;
import com.nexustasks.user.entity.User;
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
public class AuthController {

    private final AuthService authService;
    private final AuthCookieService authCookieService;
    private final SecurityUserService securityUserService;


    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        authService.verifyEmail(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(@Valid @RequestBody ResendVerificationRequest request) {
        authService.resendVerification(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
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
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        User currentUser = securityUserService.getCurrentUser();
        authService.logout(currentUser);
        authCookieService.clearAuthCookies(response);
        return ResponseEntity.noContent().build();
    }
}