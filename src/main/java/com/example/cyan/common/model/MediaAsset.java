package com.example.cyan.common.model;

import com.example.cyan.common.model.enums.MediaType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MediaAsset {

    @NotNull
    private MediaType mediaType;

    @NotBlank
    @Size(max = 5_000_000)
    private String url;

    @Size(max = 5_000_000)
    private String thumbnailUrl;

    @Size(max = 255)
    private String altText;

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }
}
