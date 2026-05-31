package com.example.cyan.content.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cyan.content.model.LandingPageTheme;
import com.example.cyan.content.service.LandingPageThemeService;

@Validated
@RestController
@RequestMapping("/api/public/landing-theme")
public class LandingPageThemeController {

    private final LandingPageThemeService landingPageThemeService;

    public LandingPageThemeController(LandingPageThemeService landingPageThemeService) {
        this.landingPageThemeService = landingPageThemeService;
    }

    @GetMapping
    public LandingPageTheme findActive() {
        return landingPageThemeService.findActive();
    }

    @GetMapping("/slug/{slug}")
    public LandingPageTheme findBySlug(@PathVariable String slug) {
        return landingPageThemeService.findBySlug(slug);
    }
}
