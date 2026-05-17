package com.example.cyan.storefront.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.example.cyan.common.model.MediaAsset;

public class CollectionDetailResponse {

    private String id;
    private String name;
    private String slug;
    private String summary;
    private String description;
    private MediaAsset coverMedia;
    private boolean featured;
    private int displayOrder;
    private Instant publishedAt;
    private List<ProductCardResponse> products = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MediaAsset getCoverMedia() {
        return coverMedia;
    }

    public void setCoverMedia(MediaAsset coverMedia) {
        this.coverMedia = coverMedia;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public List<ProductCardResponse> getProducts() {
        return products;
    }

    public void setProducts(List<ProductCardResponse> products) {
        this.products = products;
    }
}
