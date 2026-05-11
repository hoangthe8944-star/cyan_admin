package com.example.cyan.content.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.cyan.common.model.BaseDocument;
import com.example.cyan.common.model.MediaAsset;
import com.example.cyan.common.model.SeoMetadata;
import com.example.cyan.common.model.enums.EditorialStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Document("editorials")
public class Editorial extends BaseDocument {

    @NotBlank
    @Size(max = 180)
    private String title;

    @Indexed(unique = true)
    @NotBlank
    @Size(max = 200)
    private String slug;

    @Size(max = 500)
    private String summary;

    private String body;

    @NotEmpty
    private List<String> topics = new ArrayList<>();

    @Valid
    private MediaAsset coverMedia;

    @Valid
    private List<EditorialSection> sections = new ArrayList<>();

    @NotNull
    private EditorialStatus status = EditorialStatus.DRAFT;

    private Instant publishedAt;

    @Valid
    private SeoMetadata seo;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public MediaAsset getCoverMedia() {
        return coverMedia;
    }

    public void setCoverMedia(MediaAsset coverMedia) {
        this.coverMedia = coverMedia;
    }

    public List<EditorialSection> getSections() {
        return sections;
    }

    public void setSections(List<EditorialSection> sections) {
        this.sections = sections;
    }

    public EditorialStatus getStatus() {
        return status;
    }

    public void setStatus(EditorialStatus status) {
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
