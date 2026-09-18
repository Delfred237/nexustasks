package com.nexustasks.user.controller;

import com.nexustasks.auth.dto.UserResponse;
import com.nexustasks.security.service.SecurityUserService;
import com.nexustasks.user.dto.ChangePasswordRequest;
import com.nexustasks.user.dto.ChangePasswordResponse;
import com.nexustasks.user.dto.UpdateProfileRequest;
import com.nexustasks.user.entity.User;
import com.nexustasks.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Profil de l'utilisateur courant")
public class UserController {

    private final SecurityUserService securityUserService;
    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Obtenir le profil de l'utilisateur courant")
    public ResponseEntity<UserResponse> me() {
        return ResponseEntity.ok(UserResponse.from(securityUserService.getCurrentUser()));
    }

    @PatchMapping("/me")
    @Operation(
            summary = "Mettre à jour le profil",
            description = "Met à jour les informations personnelles (prénom, nom) de l'utilisateur courant."
    )
    public ResponseEntity<UserResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        User currentUser = securityUserService.getCurrentUser();
        return ResponseEntity.ok(userService.updateProfile(currentUser, request));
    }

    @PatchMapping("/me/password")
    @Operation(
            summary = "Changer le mot de passe",
            description = "Change le mot de passe après vérification de l'ancien. Révoque toutes les autres sessions."
    )
    public ResponseEntity<ChangePasswordResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        User currentUser = securityUserService.getCurrentUser();
        return ResponseEntity.ok(userService.changePassword(currentUser, request));
    }
}