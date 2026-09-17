package com.nexustasks.common.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {


    @Override
    public Optional<String> getCurrentAuditor() {
        // TODO: Phase 5 - Remplacer par la lecture du Spring SecurityContext (JWT)
        return Optional.of("system");
    }
}
