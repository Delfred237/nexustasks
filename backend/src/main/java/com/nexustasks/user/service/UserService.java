package com.nexustasks.user.service;

import com.nexustasks.audit.entity.AuditAction;
import com.nexustasks.audit.event.AuditEvent;
import com.nexustasks.auth.dto.UserResponse;
import com.nexustasks.auth.repository.RefreshTokenRepository;
import com.nexustasks.common.exception.BusinessException;
import com.nexustasks.common.exception.ErrorCode;
import com.nexustasks.user.dto.ChangePasswordRequest;
import com.nexustasks.user.dto.ChangePasswordResponse;
import com.nexustasks.user.dto.UpdateProfileRequest;
import com.nexustasks.user.entity.User;
import com.nexustasks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Met à jour les informations personnelles de l'utilisateur.
     *
     * @param user Utilisateur courant (récupéré via SecurityUserService)
     * @param request Données de mise à jour
     * @return Profil mis à jour
     */
    @Transactional
    public UserResponse updateProfile(User user, UpdateProfileRequest request) {
        log.debug("Updating profile for user {}", user.getEmail());

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        User updated = userRepository.save(user);

        // Publier l'événement d'audit
        eventPublisher.publishEvent(new AuditEvent(
                this,
                AuditAction.PROFILE_UPDATED,
                user.getPublicId(),
                user.getEmail(),
                "USER",
                user.getPublicId(),
                null,
                "Profile updated: firstName=" + request.firstName() + ", lastName=" + request.lastName()
        ));

        log.info("Profile updated for user {}", user.getEmail());
        return UserResponse.from(updated);
    }

    /**
     * Change le mot de passe de l'utilisateur.
     *
     * Sécurité :
     * - Vérifie que l'ancien mot de passe est correct
     * - Vérifie que le nouveau mot de passe est différent de l'ancien
     * - Révoque tous les refresh tokens (déconnecte les autres sessions)
     *
     * @param user Utilisateur courant
     * @param request Ancien et nouveau mot de passe
     * @return Confirmation de changement
     */
    @Transactional
    public ChangePasswordResponse changePassword(User user, ChangePasswordRequest request) {
        log.debug("Password change requested for user {}", user.getEmail());

        // 1. Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            log.warn("Failed password change attempt for user {} - wrong current password", user.getEmail());
            throw new BusinessException(ErrorCode.WRONG_CURRENT_PASSWORD);
        }

        // 2. Vérifier que le nouveau mot de passe est différent
        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.PASSWORD_REUSED);
        }

        // 3. Mettre à jour le mot de passe
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        // 4. Révoquer TOUS les refresh tokens (déconnecte toutes les sessions)
        // C'est une bonne pratique de sécurité après un changement de mot de passe
        refreshTokenRepository.deleteByUserId(user.getId());

        // 5. Publier l'événement d'audit
        eventPublisher.publishEvent(new AuditEvent(
                this,
                AuditAction.PASSWORD_CHANGED,
                user.getPublicId(),
                user.getEmail(),
                "USER",
                user.getPublicId(),
                null,
                "Password changed successfully"
        ));

        log.info("Password changed successfully for user {}", user.getEmail());
        return new ChangePasswordResponse(
                "Password changed successfully. All other sessions have been revoked.",
                true
        );
    }
}