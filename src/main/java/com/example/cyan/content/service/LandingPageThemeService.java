package com.example.cyan.content.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.cyan.common.exception.ResourceNotFoundException;
import com.example.cyan.common.util.SlugUtils;
import com.example.cyan.content.model.LandingPageTheme;
import com.example.cyan.content.repository.LandingPageThemeRepository;

@Service
public class LandingPageThemeService {

    private final LandingPageThemeRepository landingPageThemeRepository;

    public LandingPageThemeService(LandingPageThemeRepository landingPageThemeRepository) {
        this.landingPageThemeRepository = landingPageThemeRepository;
    }

    public LandingPageTheme create(LandingPageTheme theme) {
        prepareTheme(theme);
        return landingPageThemeRepository.save(theme);
    }

    public List<LandingPageTheme> findAll() {
        return landingPageThemeRepository.findAll().stream()
                .sorted(Comparator.comparing(LandingPageTheme::isActive).reversed()
                        .thenComparing(LandingPageTheme::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    public LandingPageTheme findById(String id) {
        return landingPageThemeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Landing page theme not found: " + id));
    }

    public LandingPageTheme findBySlug(String slug) {
        return landingPageThemeRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Landing page theme not found with slug: " + slug));
    }

    public LandingPageTheme findActive() {
        return landingPageThemeRepository.findFirstByActiveTrue()
                .orElseThrow(() -> new ResourceNotFoundException("No active landing page theme found"));
    }

    public LandingPageTheme update(String id, LandingPageTheme theme) {
        LandingPageTheme existing = findById(id);
        theme.setId(existing.getId());
        theme.setCreatedAt(existing.getCreatedAt());
        theme.setVersion(existing.getVersion());
        prepareTheme(theme);
        return landingPageThemeRepository.save(theme);
    }

    public LandingPageTheme activate(String id) {
        LandingPageTheme theme = findById(id);
        deactivateOtherThemes(theme.getId());
        theme.setActive(true);
        return landingPageThemeRepository.save(theme);
    }

    public void delete(String id) {
        findById(id);
        landingPageThemeRepository.deleteById(id);
    }

    private void prepareTheme(LandingPageTheme theme) {
        theme.setSlug(resolveSlug(theme.getSlug(), theme.getName()));
        if (theme.isActive()) {
            deactivateOtherThemes(theme.getId());
        }
    }

    private void deactivateOtherThemes(String currentThemeId) {
        landingPageThemeRepository.findByActiveTrue().stream()
                .filter(theme -> currentThemeId == null || !theme.getId().equals(currentThemeId))
                .forEach(theme -> {
                    theme.setActive(false);
                    landingPageThemeRepository.save(theme);
                });
    }

    private String resolveSlug(String slug, String fallbackName) {
        String resolved = SlugUtils.toSlug(slug);
        return resolved != null ? resolved : SlugUtils.toSlug(fallbackName);
    }
}
