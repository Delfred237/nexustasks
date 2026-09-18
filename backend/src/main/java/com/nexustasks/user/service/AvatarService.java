package com.nexustasks.user.service;

import com.nexustasks.storage.exception.FileStorageException;
import com.nexustasks.storage.service.FileStorageService;
import com.nexustasks.user.entity.User;
import com.nexustasks.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvatarService {

    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;
    private final Tika tika = new Tika(); // Instance thread-safe

    @Value("${app.storage.max-file-size:3MB}")
    private String maxFileSize;

    // Types MIME autorisés pour les avatars
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    // Extensions autorisées
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    private static final long MAX_SIZE_BYTES = 3 * 1024 * 1024; // 2 MB

    @Transactional
    public String uploadAvatar(User user, MultipartFile file) {
        // 1. Validation de base
        validateFile(file);

        // 2. Validation du contenu réel (magic numbers)
        String detectedMimeType = detectMimeType(file);
        if (!ALLOWED_MIME_TYPES.contains(detectedMimeType)) {
            throw new FileStorageException("Invalid file type. Only JPEG, PNG, GIF, and WebP images are allowed.");
        }

        // 3. Supprimer l'ancien avatar s'il existe
        if (user.getAvatarPath() != null && !user.getAvatarPath().isBlank()) {
            try {
                fileStorageService.delete(user.getAvatarPath());
            } catch (Exception e) {
                // On logue mais on ne bloque pas l'upload du nouvel avatar
                log.warn("Failed to delete old avatar: {}", e.getMessage());
            }
        }

        // 4. Stocker le nouveau fichier
        try {
            String filePath = fileStorageService.store(
                    file.getInputStream(),
                    file.getOriginalFilename(),
                    detectedMimeType,
                    file.getSize()
            );

            // 5. Mettre à jour l'utilisateur
            user.setAvatarPath(filePath);
            userRepository.save(user);

            log.info("Avatar uploaded for user {}: {}", user.getEmail(), filePath);
            return fileStorageService.getPublicUrl(filePath);
        } catch (IOException e) {
            throw new FileStorageException("Failed to read file", e);
        }
    }

    @Transactional
    public void deleteAvatar(User user) {
        if (user.getAvatarPath() != null && !user.getAvatarPath().isBlank()) {
            try {
                fileStorageService.delete(user.getAvatarPath());
            } catch (Exception e) {
                log.warn("Failed to delete avatar file: {}", e.getMessage());
            }
            user.setAvatarPath(null);
            userRepository.save(user);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("File is empty or missing");
        }

        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new FileStorageException("File size exceeds the maximum allowed size of 3MB");
        }

        // Validation de l'extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new FileStorageException("Filename is missing");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new FileStorageException("Invalid file extension. Allowed: " + ALLOWED_EXTENSIONS);
        }
    }

    private String detectMimeType(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return tika.detect(inputStream);
        } catch (IOException e) {
            throw new FileStorageException("Failed to analyze file content", e);
        }
    }
}