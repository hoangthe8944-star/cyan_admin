package com.example.cyan.catalog.dto;

import java.util.List;

import com.example.cyan.common.model.MediaAsset;

public record ProductVariantStyleDetailResponse(
        String variantCode,
        String styleCode,
        String name,
        String description,
        List<MediaAsset> images) {
}
