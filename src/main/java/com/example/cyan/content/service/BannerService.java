package com.example.cyan.content.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.cyan.common.exception.ResourceNotFoundException;
import com.example.cyan.common.model.enums.BannerPlacement;
import com.example.cyan.common.util.SlugUtils;
import com.example.cyan.content.model.Banner;
import com.example.cyan.content.repository.BannerRepository;

@Service
public class BannerService {

    private final BannerRepository bannerRepository;

    public BannerService(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    public Banner create(Banner banner) {
        banner.setSlug(resolveSlug(banner.getSlug(), banner.getTitle()));
        return bannerRepository.save(banner);
    }

    public List<Banner> findAll() {
        return bannerRepository.findAll();
    }

    public List<Banner> findActiveByPlacement(BannerPlacement placement) {
        return bannerRepository.findByPlacementAndActiveOrderByDisplayOrderAsc(placement, true);
    }

    public Banner findById(String id) {
        return bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner not found: " + id));
    }

    public Banner update(String id, Banner banner) {
        Banner existing = findById(id);
        banner.setId(existing.getId());
        banner.setCreatedAt(existing.getCreatedAt());
        banner.setVersion(existing.getVersion());
        banner.setSlug(resolveSlug(banner.getSlug(), banner.getTitle()));
        return bannerRepository.save(banner);
    }

    public void delete(String id) {
        findById(id);
        bannerRepository.deleteById(id);
    }

    private String resolveSlug(String slug, String fallbackName) {
        String resolved = SlugUtils.toSlug(slug);
        return resolved != null ? resolved : SlugUtils.toSlug(fallbackName);
    }
}
