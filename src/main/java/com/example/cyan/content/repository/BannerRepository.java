package com.example.cyan.content.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.cyan.common.model.enums.BannerPlacement;
import com.example.cyan.content.model.Banner;

public interface BannerRepository extends MongoRepository<Banner, String> {

    Optional<Banner> findBySlug(String slug);

    List<Banner> findByPlacementAndActiveOrderByDisplayOrderAsc(BannerPlacement placement, boolean active);

    long countByActive(boolean active);
}
