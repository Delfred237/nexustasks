package com.nexustasks.auth.repository;

import com.nexustasks.auth.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    // Récupère le dernier code actif (non utilisé) d'un utilisateur
    Optional<VerificationCode> findFirstByUserIdAndUsedFalseOrderByCreatedAtDesc(Long userId);

    // Vider le cache Hibernate apres le DELETE
    @Modifying(clearAutomatically = true)
    void deleteByUserId(Long userId);
}