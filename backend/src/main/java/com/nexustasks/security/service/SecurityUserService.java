package com.nexustasks.security.service;

import com.nexustasks.user.entity.User;
import com.nexustasks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityUserService {

    private final UserRepository userRepository;

    /**
     * Retourne l'entité User courante depuis le SecurityContext.
     * Utile dans les controllers/services pour obtenir l'utilisateur authentifié
     * sans avoir à recharger depuis la DB à chaque fois manuellement.
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user in context");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Current user not found: " + email));
    }
}
