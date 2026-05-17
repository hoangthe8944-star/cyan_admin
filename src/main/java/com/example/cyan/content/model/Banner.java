package com.example.cyan.content.model;

import java.time.Instant;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.cyan.common.model.BaseDocument;
import com.example.cyan.common.model.MediaAsset;
import com.example.cyan.common.model.enums.BannerPlacement;
import com.example.cyan.common.util.TextSanitizer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Document("banners")
public class Banner extends BaseDocument {

    @NotBlank
    @Size(max = 160)
    private String title;

    @Indexed(unique = true)
    @NotBlank
    @Size(max = 180)
    private String slug;

    @NotNull
    private BannerPlacement placement;

    @Valid
    @NotNull
    private MediaAsset media;

    @Size(max = 500)
    private String redirectUrl;

    @Size(max = 160)
    private String ctaLabel;

    private boolean active = true;

    @Min(0)
    private int displayOrder;

    private Instant startsAt;

    private Instant endsAt;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = TextSanitizer.cleanPlainText(title);
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = TextSanitizer.cleanPlainText(slug);
    }

    public BannerPlacement getPlacement() {
        return placement;
    }

    public void setPlacement(BannerPlacement placement) {
        this.placement = placement;
    }

    public MediaAsset getMedia() {
        return media;
    }

    public void setMedia(MediaAsset media) {
        this.media = media;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = TextSanitizer.cleanPlainText(redirectUrl);
    }

    public String getCtaLabel() {
        return ctaLabel;
    }

    public void setCtaLabel(String ctaLabel) {
        this.ctaLabel = TextSanitizer.cleanPlainText(ctaLabel);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Instant getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(Instant startsAt) {
        this.startsAt = startsAt;
    }

    public Instant getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(Instant endsAt) {
        this.endsAt = endsAt;
    }
}
