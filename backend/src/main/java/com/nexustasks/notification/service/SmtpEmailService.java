package com.nexustasks.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;

@Service
@Profile({"prod", "mail"}) // Actif en prod, ou en dev si on active le profil "mail"
@RequiredArgsConstructor
@Slf4j
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.from-name}")
    private String fromName;

    @Override
    public void sendVerificationEmail(String to, String firstName, String otpCode) {
        try {
            // 1. Préparer les variables du template
            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("otpCode", otpCode);

            // 2. Rendre le template Thymeleaf en HTML
            String html = templateEngine.process("email/verification-email", context);

            // 3. Envoyer via JavaMail
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject("Verify your NexusTasks account");
            helper.setText(html, true); // true = contenu HTML

            mailSender.send(message);

            // IMPORTANT : on ne loggue JAMAIS le otpCode en production
            log.info("Verification email sent to {}", to);

        } catch (MessagingException e) {
            log.error("Failed to send verification email to {}", to, e);
            throw new IllegalStateException("Email delivery failed", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}