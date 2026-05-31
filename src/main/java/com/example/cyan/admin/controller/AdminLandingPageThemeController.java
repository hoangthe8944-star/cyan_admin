package com.example.cyan.admin.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.cyan.common.model.MediaAsset;
import com.example.cyan.common.model.enums.MediaType;
import com.example.cyan.common.service.CloudinaryMediaStorageService;
import com.example.cyan.content.model.LandingPageTheme;
import com.example.cyan.content.service.LandingPageThemeService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/admin/landing-themes")
public class AdminLandingPageThemeController {

    private final LandingPageThemeService landingPageThemeService;
    private final CloudinaryMediaStorageService cloudinaryMediaStorageService;

    public AdminLandingPageThemeController(LandingPageThemeService landingPageThemeService,
            CloudinaryMediaStorageService cloudinaryMediaStorageService) {
        this.landingPageThemeService = landingPageThemeService;
        this.cloudinaryMediaStorageService = cloudinaryMediaStorageService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LandingPageTheme create(@Valid @RequestBody LandingPageTheme theme) {
        return landingPageThemeService.create(theme);
    }

    @GetMapping
    public List<LandingPageTheme> findAll() {
        return landingPageThemeService.findAll();
    }

    @GetMapping("/active")
    public LandingPageTheme findActive() {
        return landingPageThemeService.findActive();
    }

    @GetMapping("/{id}")
    public LandingPageTheme findById(@PathVariable String id) {
        return landingPageThemeService.findById(id);
    }

    @GetMapping("/slug/{slug}")
    public LandingPageTheme findBySlug(@PathVariable String slug) {
        return landingPageThemeService.findBySlug(slug);
    }

    @PostMapping("/upload-media")
    @ResponseStatus(HttpStatus.CREATED)
    public ThemeMediaUploadResponse uploadMedia(@RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "landing-themes") String folder,
            @RequestParam(required = false) String altText) {
        CloudinaryMediaStorageService.StoredFile storedFile = cloudinaryMediaStorageService.store(file, folder);

        MediaAsset mediaAsset = new MediaAsset();
        mediaAsset.setMediaType("video".equals(storedFile.resourceType()) ? MediaType.MP4 : MediaType.IMAGE);
        mediaAsset.setUrl(storedFile.url());
        mediaAsset.setThumbnailUrl(storedFile.url());
        mediaAsset.setAltText(altText);

        return new ThemeMediaUploadResponse(
                mediaAsset,
                storedFile.filename(),
                storedFile.contentType(),
                storedFile.size(),
                storedFile.resourceType());
    }

    @PutMapping("/{id}")
    public LandingPageTheme update(@PathVariable String id, @Valid @RequestBody LandingPageTheme theme) {
        return landingPageThemeService.update(id, theme);
    }

    @PostMapping("/{id}/activate")
    public LandingPageTheme activate(@PathVariable String id) {
        return landingPageThemeService.activate(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        landingPageThemeService.delete(id);
    }

    public record ThemeMediaUploadResponse(
            MediaAsset media,
            String filename,
            String contentType,
            long size,
            String resourceType) {
    }
}
