package com.nexustasks.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class AuthCookieService {

    private static final String ACCESS_TOKEN_COOKIE = "access_token";
    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpirationMs;

    @Value("${app.cookie.secure:false}")
    private boolean secure; // true en production (HTTPS)

    @Value("${app.cookie.same-site:Lax}")
    private String sameSite;

    public void addAccessTokenCookie(HttpServletResponse response, String token) {
        addCookie(response, ACCESS_TOKEN_COOKIE, token, (int) (accessTokenExpirationMs / 1000));
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String token) {
        addCookie(response, REFRESH_TOKEN_COOKIE, token, (int) (refreshTokenExpirationMs / 1000));
    }

    public void clearAuthCookies(HttpServletResponse response) {
        addCookie(response, ACCESS_TOKEN_COOKIE, "", 0);
        addCookie(response, REFRESH_TOKEN_COOKIE, "", 0);
    }

    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> REFRESH_TOKEN_COOKIE.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        // Utilisation du format Set-Cookie brut pour supporter SameSite
        // (l'API Cookie de Servlet ne supporte pas SameSite nativement)
        String sameSiteValue = secure ? "None" : sameSite;
        String cookieHeader = String.format(
                "%s=%s; Max-Age=%d; Path=/; HttpOnly; %s SameSite=%s",
                name,
                value,
                maxAge,
                secure ? "Secure;" : "",
                sameSiteValue
        );
        response.addHeader("Set-Cookie", cookieHeader);
    }
}
