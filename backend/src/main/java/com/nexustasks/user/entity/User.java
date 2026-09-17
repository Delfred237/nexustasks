package com.nexustasks.user.entity;

import com.nexustasks.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.USER;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = false; // Désactivé tant que l'email n'est pas vérifié

    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private boolean emailVerified = false;

    @Column(length = 500)
    private String avatarUrl;

    // Méthode utilitaire pour masquer le mot de passe dans les logs/toString
    @Override
    public String toString() {
        return "User{" +
                "publicId='" + getPublicId() + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}