package com.example.cyan.common.service;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.cyan.common.exception.BadRequestException;
import com.example.cyan.common.util.SlugUtils;

@Service
public class CloudinaryMediaStorageService {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");
    private static final Set<String> ALLOWED_VIDEO_TYPES = Set.of("video/mp4");

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${cloudinary.api-key:}")
    private String apiKey;

    @Value("${cloudinary.api-secret:}")
    private String apiSecret;

    @Value("${cloudinary.image-folder:products/images}")
    private String imageFolder;

    @Value("${cloudinary.video-folder:banners/videos}")
    private String videoFolder;

    public StoredFile store(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Please choose a file to upload");
        }

        String contentType = file.getContentType();
        validateContentType(contentType);

        if (!isConfigured()) {
            throw new BadRequestException("Cloudinary is not configured for media uploads");
        }

        try {
            String originalFilename = file.getOriginalFilename() == null ? "media" : file.getOriginalFilename();
            String baseName = extractBaseName(originalFilename);
            String safeBaseName = SlugUtils.toSlug(baseName);
            if (safeBaseName == null || safeBaseName.isBlank()) {
                safeBaseName = "media";
            }

            String resourceType = isVideo(contentType) ? "video" : "image";
            String publicId = safeBaseName + "-" + UUID.randomUUID();
            String targetFolder = resolveFolder(folder, resourceType);

            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudName,
                    "api_key", apiKey,
                    "api_secret", apiSecret,
                    "secure", true));

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", resourceType,
                            "folder", targetFolder,
                            "public_id", publicId));

            Object secureUrl = uploadResult.get("secure_url");
            if (!(secureUrl instanceof String url) || url.isBlank()) {
                throw new IllegalStateException("Cloudinary did not return a media URL");
            }

            return new StoredFile(
                    url,
                    publicId,
                    contentType,
                    file.getSize(),
                    resourceType);
        } catch (IOException ex) {
            throw new BadRequestException("Failed to read uploaded file");
        } catch (Exception ex) {
            throw new BadRequestException("Failed to upload file to Cloudinary");
        }
    }

    private void validateContentType(String contentType) {
        if (contentType == null) {
            throw new BadRequestException("Uploaded file is missing content type");
        }

        if (!ALLOWED_IMAGE_TYPES.contains(contentType) && !ALLOWED_VIDEO_TYPES.contains(contentType)) {
            throw new BadRequestException("Only image files and MP4 videos are supported");
        }
    }

    private boolean isConfigured() {
        return !cloudName.isBlank() && !apiKey.isBlank() && !apiSecret.isBlank();
    }

    private boolean isVideo(String contentType) {
        return contentType != null && contentType.startsWith("video/");
    }

    private String resolveFolder(String folder, String resourceType) {
        String baseFolder = "video".equals(resourceType) ? videoFolder : imageFolder;
        String safeFolder = SlugUtils.toSlug(folder);
        if (safeFolder == null || safeFolder.isBlank()) {
            return baseFolder;
        }
        return baseFolder + "/" + safeFolder;
    }

    private String extractBaseName(String filename) {
        int extensionIndex = filename.lastIndexOf('.');
        if (extensionIndex <= 0) {
            return filename;
        }
        return filename.substring(0, extensionIndex);
    }

    public record StoredFile(String url, String filename, String contentType, long size, String resourceType) {
    }
}
