package com.example.cyan.common.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.cyan.common.exception.BadRequestException;
import com.example.cyan.common.util.SlugUtils;

@Service
public class LocalFileStorageService {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");
    private static final Set<String> ALLOWED_VIDEO_TYPES = Set.of("video/mp4");

    @Value("${app.upload-dir:./uploads}")
    private String uploadDir;

    public StoredFile store(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Please choose a file to upload");
        }

        validateContentType(file.getContentType());

        String originalFilename = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
        String extension = extractExtension(originalFilename);
        String baseName = extractBaseName(originalFilename);
        String safeBaseName = SlugUtils.toSlug(baseName);
        if (safeBaseName == null || safeBaseName.isBlank()) {
            safeBaseName = "media";
        }

        String safeFolder = sanitizeFolder(folder);
        String generatedFilename = safeBaseName + "-" + UUID.randomUUID() + extension;
        Path targetDirectory = Paths.get(uploadDir, safeFolder).toAbsolutePath().normalize();
        Path targetFile = targetDirectory.resolve(generatedFilename).normalize();

        try {
            Files.createDirectories(targetDirectory);
            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new BadRequestException("Failed to store uploaded file");
        }

        return new StoredFile(
                "/uploads/" + safeFolder + "/" + generatedFilename,
                generatedFilename,
                file.getContentType(),
                file.getSize());
    }

    private void validateContentType(String contentType) {
        if (contentType == null) {
            throw new BadRequestException("Uploaded file is missing content type");
        }

        if (!ALLOWED_IMAGE_TYPES.contains(contentType) && !ALLOWED_VIDEO_TYPES.contains(contentType)) {
            throw new BadRequestException("Only image files and MP4 videos are supported");
        }
    }

    private String sanitizeFolder(String folder) {
        String safeFolder = SlugUtils.toSlug(folder);
        return safeFolder == null || safeFolder.isBlank() ? "general" : safeFolder;
    }

    private String extractBaseName(String filename) {
        int extensionIndex = filename.lastIndexOf('.');
        if (extensionIndex <= 0) {
            return filename;
        }
        return filename.substring(0, extensionIndex);
    }

    private String extractExtension(String filename) {
        int extensionIndex = filename.lastIndexOf('.');
        if (extensionIndex < 0) {
            return "";
        }
        return filename.substring(extensionIndex).toLowerCase(Locale.ROOT);
    }

    public record StoredFile(String relativeUrl, String filename, String contentType, long size) {
    }
}
