package com.example.cyan.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.cyan.common.service.LocalFileStorageService;

@Validated
@RestController
@RequestMapping("/api/admin/uploads")
public class AdminUploadController {

    private final LocalFileStorageService localFileStorageService;

    public AdminUploadController(LocalFileStorageService localFileStorageService) {
        this.localFileStorageService = localFileStorageService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UploadResponse upload(@RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "general") String folder) {
        LocalFileStorageService.StoredFile storedFile = localFileStorageService.store(file, folder);
        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(storedFile.relativeUrl())
                .toUriString();

        return new UploadResponse(url, storedFile.filename(), storedFile.contentType(), storedFile.size());
    }

    public record UploadResponse(String url, String filename, String contentType, long size) {
    }
}
