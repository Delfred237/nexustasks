package com.nexustasks.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "security.otp")
public record OtpProperties(
        @DefaultValue("15") int ttlMinutes,
        @DefaultValue("5") int maxAttempts,
        @DefaultValue("60") int resendCooldownSeconds
) {}