package com.example.cyan.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.cyan.common.service.CloudinaryMediaStorageService;

@Validated
@RestController
@RequestMapping("/api/admin/uploads")
public class AdminUploadController {

    private final CloudinaryMediaStorageService cloudinaryMediaStorageService;

    public AdminUploadController(CloudinaryMediaStorageService cloudinaryMediaStorageService) {
        this.cloudinaryMediaStorageService = cloudinaryMediaStorageService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UploadResponse upload(@RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "general") String folder) {
        CloudinaryMediaStorageService.StoredFile storedFile = cloudinaryMediaStorageService.store(file, folder);
        return new UploadResponse(storedFile.url(), storedFile.filename(), storedFile.contentType(), storedFile.size());
    }

    public record UploadResponse(String url, String filename, String contentType, long size) {
    }
}
