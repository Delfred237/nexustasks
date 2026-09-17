package com.nexustasks.notification.service;

public interface EmailService {
    void sendVerificationEmail(String to, String firstName, String otpCode);
}