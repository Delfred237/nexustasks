package com.nexustasks.user.controller;

import com.nexustasks.security.service.SecurityUserService;
import com.nexustasks.user.dto.AvatarUploadResponse;
import com.nexustasks.user.entity.User;
import com.nexustasks.user.service.AvatarService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users/me/avatar")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Gestion de l'avatar utilisateur")
public class AvatarController {

    private final AvatarService avatarService;
    private final SecurityUserService securityUserService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<AvatarUploadResponse> uploadAvatar(@RequestParam("file") MultipartFile file) {
        User currentUser = securityUserService.getCurrentUser();
        String avatarUrl = avatarService.uploadAvatar(currentUser, file);
        return ResponseEntity.ok(new AvatarUploadResponse(avatarUrl));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAvatar() {
        User currentUser = securityUserService.getCurrentUser();
        avatarService.deleteAvatar(currentUser);
        return ResponseEntity.noContent().build();
    }
}