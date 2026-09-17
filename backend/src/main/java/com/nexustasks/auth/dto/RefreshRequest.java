package com.nexustasks.auth.dto;

/**
 * Utilisé par les clients Mobile (Flutter) qui n'ont pas de cookies.
 * Les clients Web envoient le refresh token via cookie, ce DTO sera alors vide.
 */
public record RefreshRequest(String refreshToken) {}