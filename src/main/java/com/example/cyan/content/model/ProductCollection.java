package com.example.cyan.content.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.cyan.common.model.BaseDocument;
import com.example.cyan.common.model.MediaAsset;
import com.example.cyan.common.model.SeoMetadata;
import com.example.cyan.common.model.enums.CollectionStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Document("collections")
public class ProductCollection extends BaseDocument {

    @NotBlank
    @Size(max = 180)
    private String name;

    @Indexed(unique = true)
    @NotBlank
    @Size(max = 200)
    private String slug;

    @Size(max = 500)
    private String summary;

    private String description;

    @Valid
    private MediaAsset coverMedia;

    private List<String> productIds = new ArrayList<>();

    private boolean featured;

    @Min(0)
    private int displayOrder;

    @NotNull
    private CollectionStatus status = CollectionStatus.DRAFT;

    private Instant publishedAt;

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

    public List<String> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<String> productIds) {
        this.productIds = productIds;
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

    public CollectionStatus getStatus() {
        return status;
    }

    public void setStatus(CollectionStatus status) {
        this.status = status;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public SeoMetadata getSeo() {
        return seo;
    }

    public void setSeo(SeoMetadata seo) {
        this.seo = seo;
    }
}
