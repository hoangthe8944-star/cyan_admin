package com.example.cyan.common.model;

import jakarta.validation.constraints.Size;

public class SeoMetadata {

    @Size(max = 255)
    private String title;

    @Size(max = 500)
    private String description;

    @Size(max = 500)
    private String keywords;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}
