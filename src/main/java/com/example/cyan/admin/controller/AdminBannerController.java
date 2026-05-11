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

import com.example.cyan.content.model.Banner;
import com.example.cyan.content.service.BannerService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/admin/banners")
public class AdminBannerController {

    private final BannerService bannerService;

    public AdminBannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Banner create(@Valid @RequestBody Banner banner) {
        return bannerService.create(banner);
    }

    @GetMapping
    public List<Banner> findAll() {
        return bannerService.findAll();
    }

    @GetMapping("/{id}")
    public Banner findById(@PathVariable String id) {
        return bannerService.findById(id);
    }

    @PutMapping("/{id}")
    public Banner update(@PathVariable String id, @Valid @RequestBody Banner banner) {
        return bannerService.update(id, banner);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        bannerService.delete(id);
    }
}
