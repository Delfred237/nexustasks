package com.nexustasks.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class JpaAuditingConfig {
    // Cette annotation suffit à activer le remplissage automatique
    // des champs @CreatedDate et @LastModifiedDate.
}
