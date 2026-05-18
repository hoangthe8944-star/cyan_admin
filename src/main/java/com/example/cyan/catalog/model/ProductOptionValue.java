package com.example.cyan.catalog.model;

import com.example.cyan.common.model.MediaAsset;
import com.example.cyan.common.util.TextSanitizer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProductOptionValue {

    @NotBlank
    @Size(max = 80)
    private String code;

    @NotBlank
    @Size(max = 120)
    private String label;

    @Valid
    private MediaAsset swatchMedia;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = TextSanitizer.cleanPlainText(code);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = TextSanitizer.cleanPlainText(label);
    }

    public MediaAsset getSwatchMedia() {
        return swatchMedia;
    }

    public void setSwatchMedia(MediaAsset swatchMedia) {
        this.swatchMedia = swatchMedia;
    }
}
