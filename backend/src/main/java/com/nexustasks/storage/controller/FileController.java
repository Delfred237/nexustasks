package com.nexustasks.storage.controller;

import com.nexustasks.storage.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @GetMapping("/avatars/{filename}")
    public ResponseEntity<InputStreamResource> getAvatar(@PathVariable String filename) {
        String filePath = "avatars/" + filename;
        InputStream inputStream = fileStorageService.load(filePath);

        // Déterminer le content type
        String contentType = "image/jpeg"; // défaut
        if (filename.endsWith(".png")) contentType = "image/png";
        else if (filename.endsWith(".gif")) contentType = "image/gif";
        else if (filename.endsWith(".webp")) contentType = "image/webp";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400") // Cache 24h
                .body(new InputStreamResource(inputStream));
    }
}