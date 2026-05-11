package com.example.cyan.catalog.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.cyan.common.model.BaseDocument;
import com.example.cyan.common.model.MediaAsset;
import com.example.cyan.common.model.SeoMetadata;
import com.example.cyan.common.model.enums.ProductStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Document("products")
public class Product extends BaseDocument {

    @NotBlank
    @Size(max = 120)
    private String name;

    @Indexed(unique = true)
    @NotBlank
    @Size(max = 160)
    private String slug;

    @Indexed(unique = true)
    @NotBlank
    @Size(max = 60)
    private String sku;

    @Size(max = 500)
    private String shortDescription;

    private String description;

    @Size(max = 120)
    private String brand;

    @Size(max = 120)
    private String material;

    @Size(max = 120)
    private String gemstone;

    @NotBlank
    private String primaryCategoryId;

    @NotEmpty
    private List<String> categoryIds = new ArrayList<>();

    private List<String> tags = new ArrayList<>();

    @Valid
    private List<MediaAsset> gallery = new ArrayList<>();

    @Valid
    @NotEmpty
    private List<ProductOption> options = new ArrayList<>();

    @Valid
    @NotEmpty
    private List<ProductVariant> variants = new ArrayList<>();

    @NotNull
    private ProductStatus status = ProductStatus.DRAFT;

    @DecimalMin("0.0")
    private BigDecimal minPrice = BigDecimal.ZERO;

    @DecimalMin("0.0")
    private BigDecimal maxPrice = BigDecimal.ZERO;

    @Min(0)
    private int totalStock;

    private boolean featured;

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

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getGemstone() {
        return gemstone;
    }

    public void setGemstone(String gemstone) {
        this.gemstone = gemstone;
    }

    public String getPrimaryCategoryId() {
        return primaryCategoryId;
    }

    public void setPrimaryCategoryId(String primaryCategoryId) {
        this.primaryCategoryId = primaryCategoryId;
    }

    public List<String> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<String> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<MediaAsset> getGallery() {
        return gallery;
    }

    public void setGallery(List<MediaAsset> gallery) {
        this.gallery = gallery;
    }

    public List<ProductOption> getOptions() {
        return options;
    }

    public void setOptions(List<ProductOption> options) {
        this.options = options;
    }

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public int getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(int totalStock) {
        this.totalStock = totalStock;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
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
