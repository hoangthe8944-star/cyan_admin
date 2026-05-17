package com.example.cyan.storefront.dto;

import java.util.ArrayList;
import java.util.List;

import com.example.cyan.content.model.Banner;

public class HomeResponse {

    private List<Banner> mainBanners = new ArrayList<>();
    private List<Banner> subBanners = new ArrayList<>();
    private List<CategoryTreeResponse> categories = new ArrayList<>();
    private List<CollectionSummaryResponse> featuredCollections = new ArrayList<>();
    private List<ProductCardResponse> featuredProducts = new ArrayList<>();
    private List<ProductCardResponse> newArrivals = new ArrayList<>();
    private List<EditorialSummaryResponse> latestEditorials = new ArrayList<>();

    public List<Banner> getMainBanners() {
        return mainBanners;
    }

    public void setMainBanners(List<Banner> mainBanners) {
        this.mainBanners = mainBanners;
    }

    public List<Banner> getSubBanners() {
        return subBanners;
    }

    public void setSubBanners(List<Banner> subBanners) {
        this.subBanners = subBanners;
    }

    public List<CategoryTreeResponse> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryTreeResponse> categories) {
        this.categories = categories;
    }

    public List<CollectionSummaryResponse> getFeaturedCollections() {
        return featuredCollections;
    }

    public void setFeaturedCollections(List<CollectionSummaryResponse> featuredCollections) {
        this.featuredCollections = featuredCollections;
    }

    public List<ProductCardResponse> getFeaturedProducts() {
        return featuredProducts;
    }

    public void setFeaturedProducts(List<ProductCardResponse> featuredProducts) {
        this.featuredProducts = featuredProducts;
    }

    public List<ProductCardResponse> getNewArrivals() {
        return newArrivals;
    }

    public void setNewArrivals(List<ProductCardResponse> newArrivals) {
        this.newArrivals = newArrivals;
    }

    public List<EditorialSummaryResponse> getLatestEditorials() {
        return latestEditorials;
    }

    public void setLatestEditorials(List<EditorialSummaryResponse> latestEditorials) {
        this.latestEditorials = latestEditorials;
    }
}
