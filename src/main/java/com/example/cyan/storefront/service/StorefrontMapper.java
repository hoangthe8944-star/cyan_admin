package com.example.cyan.storefront.service;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.example.cyan.catalog.model.Category;
import com.example.cyan.catalog.model.Product;
import com.example.cyan.content.model.Editorial;
import com.example.cyan.storefront.dto.CategoryTreeResponse;
import com.example.cyan.storefront.dto.EditorialSummaryResponse;
import com.example.cyan.storefront.dto.ProductCardResponse;

@Component
public class StorefrontMapper {

    public CategoryTreeResponse toCategoryTree(Category category) {
        CategoryTreeResponse response = new CategoryTreeResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setSlug(category.getSlug());
        response.setDescription(category.getDescription());
        response.setLevel(category.getLevel());
        response.setStatus(category.getStatus());
        response.setCoverMedia(category.getCoverMedia());
        return response;
    }

    public ProductCardResponse toProductCard(Product product) {
        ProductCardResponse response = new ProductCardResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setSlug(product.getSlug());
        response.setSku(product.getSku());
        response.setShortDescription(product.getShortDescription());
        response.setBrand(product.getBrand());
        response.setMaterial(product.getMaterial());
        response.setGemstone(product.getGemstone());
        response.setMinPrice(product.getMinPrice());
        response.setMaxPrice(product.getMaxPrice());
        response.setTotalStock(product.getTotalStock());
        response.setFeatured(product.isFeatured());
        response.setStatus(product.getStatus());
        response.setGallery(product.getGallery() == null ? new ArrayList<>() : product.getGallery());
        return response;
    }

    public EditorialSummaryResponse toEditorialSummary(Editorial editorial) {
        EditorialSummaryResponse response = new EditorialSummaryResponse();
        response.setId(editorial.getId());
        response.setTitle(editorial.getTitle());
        response.setSlug(editorial.getSlug());
        response.setSummary(editorial.getSummary());
        response.setTopics(editorial.getTopics() == null ? new ArrayList<>() : editorial.getTopics());
        response.setCoverMedia(editorial.getCoverMedia());
        response.setPublishedAt(editorial.getPublishedAt());
        return response;
    }
}
