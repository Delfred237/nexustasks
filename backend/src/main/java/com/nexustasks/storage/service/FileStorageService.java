package com.nexustasks.storage.service;

import java.io.InputStream;

/**
 * Abstraction du stockage de fichiers.
 * Permet de basculer facilement entre stockage local et cloud (GCS, S3).
 */
public interface FileStorageService {

    /**
     * Stocke un fichier et retourne son chemin relatif unique.
     */
    String store(InputStream inputStream, String originalFilename, String contentType, long size);

    /**
     * Supprime un fichier par son chemin relatif.
     */
    void delete(String filePath);

    /**
     * Retourne le contenu du fichier (pour le servir au client).
     */
    InputStream load(String filePath);

    /**
     * Retourne l'URL publique pour accéder au fichier (peut être une URL signée en cloud).
     */
    String getPublicUrl(String filePath);
}