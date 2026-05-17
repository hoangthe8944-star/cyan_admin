package com.example.cyan.common.model;

import java.util.stream.StreamSupport;

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

    public void setKeywords(Object keywords) {
        if (keywords == null) {
            this.keywords = null;
            return;
        }

        if (keywords instanceof Iterable<?> values) {
            String normalized = StreamSupport.stream(values.spliterator(), false)
                    .map(value -> value == null ? null : value.toString())
                    .map(String::trim)
                    .filter(value -> !value.isBlank())
                    .collect(java.util.stream.Collectors.joining(", "));
            this.keywords = normalized.isBlank() ? null : normalized;
            return;
        }

        String normalized = keywords.toString().trim();
        this.keywords = normalized.isBlank() ? null : normalized;
    }
}
