package com.example.cyan.catalog.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.cyan.common.model.BaseDocument;
import com.example.cyan.common.model.MediaAsset;
import com.example.cyan.common.model.SeoMetadata;
import com.example.cyan.common.model.enums.CategoryStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Document("categories")
public class Category extends BaseDocument {

    @NotBlank
    @Size(max = 120)
    private String name;

    @Indexed(unique = true)
    @NotBlank
    @Size(max = 160)
    private String slug;

    @Size(max = 500)
    private String description;

    private String parentId;

    private String rootCategoryId;

    @Min(1)
    @Max(2)
    private int level = 1;

    @NotNull
    private CategoryStatus status = CategoryStatus.ACTIVE;

    @Min(0)
    private int displayOrder;

    @Valid
    private MediaAsset coverMedia;

    @Valid
    private SeoMetadata seo;

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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getRootCategoryId() {
        return rootCategoryId;
    }

    public void setRootCategoryId(String rootCategoryId) {
        this.rootCategoryId = rootCategoryId;
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

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public MediaAsset getCoverMedia() {
        return coverMedia;
    }

    public void setCoverMedia(MediaAsset coverMedia) {
        this.coverMedia = coverMedia;
    }

    public SeoMetadata getSeo() {
        return seo;
    }

    public void setSeo(SeoMetadata seo) {
        this.seo = seo;
    }
}
