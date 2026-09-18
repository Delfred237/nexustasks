package com.nexustasks.user.controller;

import com.nexustasks.auth.dto.UserResponse;
import com.nexustasks.security.service.SecurityUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Profil de l'utilisateur courant")
public class UserController {

    private final SecurityUserService securityUserService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        return ResponseEntity.ok(UserResponse.from(securityUserService.getCurrentUser()));
    }
}
