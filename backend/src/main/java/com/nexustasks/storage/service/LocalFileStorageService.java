package com.nexustasks.storage.service;

import com.nexustasks.storage.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class LocalFileStorageService implements FileStorageService {

    private final Path basePath;

    public LocalFileStorageService(@Value("${app.storage.local.base-path}") String basePath) {
        this.basePath = Paths.get(basePath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.basePath);
            log.info("Storage base path initialized at: {}", this.basePath);
        } catch (IOException e) {
            throw new FileStorageException("Could not create storage directory", e);
        }
    }

    @Override
    public String store(InputStream inputStream, String originalFilename, String contentType, long size) {
        try {
            // Générer un nom de fichier unique et sûr
            String extension = getExtension(contentType);
            String fileName = UUID.randomUUID().toString() + extension;

            // Organiser par sous-dossier "avatars"
            String relativePath = "avatars/" + fileName;
            Path targetPath = this.basePath.resolve(relativePath).normalize();

            // Sécurité : vérifier que le chemin résolu est bien dans le basePath (anti path traversal)
            if (!targetPath.startsWith(this.basePath)) {
                throw new FileStorageException("Invalid file path: path traversal detected");
            }

            // Créer le sous-dossier si nécessaire
            Files.createDirectories(targetPath.getParent());

            // Copier le fichier
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("File stored successfully: {}", relativePath);

            return relativePath;
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file", e);
        }
    }

    @Override
    public void delete(String filePath) {
        try {
            Path path = this.basePath.resolve(filePath).normalize();
            // Sécurité : anti path traversal
            if (!path.startsWith(this.basePath)) {
                throw new FileStorageException("Invalid file path: path traversal detected");
            }
            Files.deleteIfExists(path);
            log.info("File deleted: {}", filePath);
        } catch (IOException e) {
            throw new FileStorageException("Failed to delete file", e);
        }
    }

    @Override
    public InputStream load(String filePath) {
        try {
            Path path = this.basePath.resolve(filePath).normalize();
            if (!path.startsWith(this.basePath)) {
                throw new FileStorageException("Invalid file path: path traversal detected");
            }
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new FileStorageException("Failed to load file", e);
        }
    }

    @Override
    public String getPublicUrl(String filePath) {
        // En local, on retourne un chemin relatif que le frontend pourra utiliser
        // pour construire l'URL complète via un endpoint dédié
        return "/api/files/" + filePath;
    }

    private String getExtension(String contentType) {
        if (contentType == null) return ".bin";
        return switch (contentType.toLowerCase()) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> ".bin";
        };
    }
}