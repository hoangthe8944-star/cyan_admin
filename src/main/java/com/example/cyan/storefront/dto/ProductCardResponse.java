package com.example.cyan.storefront.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.cyan.common.model.MediaAsset;
import com.example.cyan.common.model.enums.ProductStatus;

public class ProductCardResponse {

    private String id;
    private String name;
    private String slug;
    private String sku;
    private String shortDescription;
    private String brand;
    private String material;
    private String gemstone;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private int totalStock;
    private boolean featured;
    private ProductStatus status;
    private List<MediaAsset> gallery = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public List<MediaAsset> getGallery() {
        return gallery;
    }

    public void setGallery(List<MediaAsset> gallery) {
        this.gallery = gallery;
    }
}
