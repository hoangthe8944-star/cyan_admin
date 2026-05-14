package com.example.cyan.common.service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.cyan.common.exception.BadRequestException;
import com.example.cyan.common.util.SlugUtils;

@Service
public class CloudinaryVideoStorageService {

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${cloudinary.api-key:}")
    private String apiKey;

    @Value("${cloudinary.api-secret:}")
    private String apiSecret;

    @Value("${cloudinary.video-folder:banners/videos}")
    private String videoFolder;

    public StoredFile store(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Please choose a file to upload");
        }

        if (!isConfigured()) {
            throw new BadRequestException("Cloudinary is not configured for video uploads");
        }

        try {
            String originalFilename = file.getOriginalFilename() == null ? "video" : file.getOriginalFilename();
            String baseName = extractBaseName(originalFilename);
            String safeBaseName = SlugUtils.toSlug(baseName);
            if (safeBaseName == null || safeBaseName.isBlank()) {
                safeBaseName = "video";
            }

            String publicId = safeBaseName + "-" + UUID.randomUUID();
            String targetFolder = resolveFolder(folder);

            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudName,
                    "api_key", apiKey,
                    "api_secret", apiSecret,
                    "secure", true));

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "video",
                            "folder", targetFolder,
                            "public_id", publicId));

            Object secureUrl = uploadResult.get("secure_url");
            if (!(secureUrl instanceof String url) || url.isBlank()) {
                throw new IllegalStateException("Cloudinary did not return a video URL");
            }

            return new StoredFile(
                    url,
                    publicId,
                    file.getContentType(),
                    file.getSize());
        } catch (IOException ex) {
            throw new BadRequestException("Failed to read uploaded video");
        } catch (Exception ex) {
            throw new BadRequestException("Failed to upload video to Cloudinary");
        }
    }

    private boolean isConfigured() {
        return !cloudName.isBlank() && !apiKey.isBlank() && !apiSecret.isBlank();
    }

    private String resolveFolder(String folder) {
        String safeFolder = SlugUtils.toSlug(folder);
        if (safeFolder == null || safeFolder.isBlank()) {
            return videoFolder;
        }
        return videoFolder + "/" + safeFolder;
    }

    private String extractBaseName(String filename) {
        int extensionIndex = filename.lastIndexOf('.');
        if (extensionIndex <= 0) {
            return filename;
        }
        return filename.substring(0, extensionIndex);
    }

    public record StoredFile(String url, String filename, String contentType, long size) {
    }
}
