package com.nexustasks.common.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {


    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.of("system");
        }

        // Le principal est soit notre User custom, soit le UserDetails de Spring
        // Pour simplifier, on retourne l'email comme identifiant d'audit, ou on pourrait charger l'User pour avoir le publicId.
        return Optional.of(authentication.getName()); // authentication.getName() retourne l'email (username)
    }
}
