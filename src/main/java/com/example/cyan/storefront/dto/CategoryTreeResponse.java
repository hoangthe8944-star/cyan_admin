package com.example.cyan.storefront.dto;

import java.util.ArrayList;
import java.util.List;

import com.example.cyan.common.model.MediaAsset;
import com.example.cyan.common.model.enums.CategoryStatus;

public class CategoryTreeResponse {

    private String id;
    private String name;
    private String slug;
    private String description;
    private int level;
    private CategoryStatus status;
    private MediaAsset coverMedia;
    private List<CategoryTreeResponse> children = new ArrayList<>();

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public CategoryStatus getStatus() {
        return status;
    }

    public void setStatus(CategoryStatus status) {
        this.status = status;
    }

    public MediaAsset getCoverMedia() {
        return coverMedia;
    }

    public void setCoverMedia(MediaAsset coverMedia) {
        this.coverMedia = coverMedia;
    }

    public List<CategoryTreeResponse> getChildren() {
        return children;
    }

    public void setChildren(List<CategoryTreeResponse> children) {
        this.children = children;
    }
}
