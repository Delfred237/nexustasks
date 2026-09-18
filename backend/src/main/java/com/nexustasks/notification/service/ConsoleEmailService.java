package com.nexustasks.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * DEV-ONLY : Simule l'envoi d'email en affichant le code dans la console.
 * En production, cette classe sera remplacée par une implémentation SMTP + Thymeleaf.
 * Le code OTP n'est JAMAIS loggé en production ; il ne l'est ici que pour permettre le test local.
 */
@Service
@Profile({"(dev & !mail)", "(test & !mail), (testcontainers & !mail)"})
@Slf4j
public class ConsoleEmailService implements EmailService {

    @Override
    public void sendVerificationEmail(String to, String firstName, String otpCode) {
        log.warn("");
        log.warn("╔═══════════ DEV EMAIL SIMULATOR ═══════════╗");
        log.warn("║ To      : {}", to);
        log.warn("║ Subject : Verify your NexusTasks account");
        log.warn("║                                           ");
        log.warn("║ Hello {},", firstName);
        log.warn("║ Your verification code is: {}", otpCode);
        log.warn("║ This code expires in 15 minutes.");
        log.warn("╚═══════════════════════════════════════════╝");
        log.warn("");
    }
}