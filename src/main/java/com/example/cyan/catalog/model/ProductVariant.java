package com.example.cyan.catalog.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.cyan.common.model.MediaAsset;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class ProductVariant {

    @NotBlank
    @Size(max = 80)
    private String variantCode;

    @NotBlank
    @Size(max = 80)
    private String modelCode;

    @NotBlank
    @Size(max = 80)
    private String styleCode;

    @NotEmpty
    @Valid
    private List<VariantSelection> selections = new ArrayList<>();

    @DecimalMin("0.0")
    private BigDecimal price = BigDecimal.ZERO;

    @DecimalMin("0.0")
    private BigDecimal compareAtPrice;

    @DecimalMin("0.0")
    private BigDecimal costPrice;

    @Min(0)
    private int stockQuantity;

    @DecimalMin("0.0")
    private BigDecimal weightInGram;

    private boolean active = true;

    @Valid
    private List<MediaAsset> media = new ArrayList<>();

    public String getVariantCode() {
        return variantCode;
    }

    public void setVariantCode(String variantCode) {
        this.variantCode = variantCode;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getStyleCode() {
        return styleCode;
    }

    public void setStyleCode(String styleCode) {
        this.styleCode = styleCode;
    }

    public List<VariantSelection> getSelections() {
        return selections;
    }

    public void setSelections(List<VariantSelection> selections) {
        this.selections = selections;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCompareAtPrice() {
        return compareAtPrice;
    }

    public void setCompareAtPrice(BigDecimal compareAtPrice) {
        this.compareAtPrice = compareAtPrice;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public BigDecimal getWeightInGram() {
        return weightInGram;
    }

    public void setWeightInGram(BigDecimal weightInGram) {
        this.weightInGram = weightInGram;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<MediaAsset> getMedia() {
        return media;
    }

    public void setMedia(List<MediaAsset> media) {
        this.media = media;
    }
}
