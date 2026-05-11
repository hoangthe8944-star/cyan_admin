package com.example.cyan.content.model;

import java.util.ArrayList;
import java.util.List;

import com.example.cyan.common.model.MediaAsset;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EditorialSection {

    @NotBlank
    @Size(max = 160)
    private String heading;

    @Size(max = 255)
    private String subHeading;

    private String content;

    @Min(0)
    private int displayOrder;

    @Valid
    private List<MediaAsset> media = new ArrayList<>();

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getSubHeading() {
        return subHeading;
    }

    public void setSubHeading(String subHeading) {
        this.subHeading = subHeading;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public List<MediaAsset> getMedia() {
        return media;
    }

    public void setMedia(List<MediaAsset> media) {
        this.media = media;
    }
}
