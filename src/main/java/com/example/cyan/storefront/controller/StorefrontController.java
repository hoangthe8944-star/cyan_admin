package com.example.cyan.storefront.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.cyan.common.model.enums.BannerPlacement;
import com.example.cyan.content.model.Banner;
import com.example.cyan.content.model.Editorial;
import com.example.cyan.order.model.Order;
import com.example.cyan.storefront.dto.CategoryTreeResponse;
import com.example.cyan.storefront.dto.EditorialSummaryResponse;
import com.example.cyan.storefront.dto.HomeResponse;
import com.example.cyan.storefront.dto.ProductCardResponse;
import com.example.cyan.storefront.dto.ProductCatalogResponse;
import com.example.cyan.storefront.service.StorefrontService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Validated
@RestController
@RequestMapping("/api/public")
public class StorefrontController {

    private final StorefrontService storefrontService;

    public StorefrontController(StorefrontService storefrontService) {
        this.storefrontService = storefrontService;
    }

    @GetMapping("/home")
    public HomeResponse home() {
        return storefrontService.getHome();
    }

    @GetMapping("/banners")
    public List<Banner> banners(@RequestParam BannerPlacement placement) {
        return storefrontService.activeBanners(placement);
    }

    @GetMapping("/categories")
    public List<CategoryTreeResponse> categories() {
        return storefrontService.getCategoryTree();
    }

    @GetMapping("/categories/{slug}")
    public CategoryTreeResponse categoryDetail(@PathVariable String slug) {
        return storefrontService.getCategoryBySlug(slug);
    }

    @GetMapping("/categories/{slug}/products")
    public ProductCatalogResponse categoryProducts(@PathVariable String slug,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean featured) {
        return storefrontService.filterActiveProducts(keyword, slug, minPrice, maxPrice, featured);
    }

    @GetMapping("/products")
    public ProductCatalogResponse products(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categorySlug,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean featured) {
        return storefrontService.filterActiveProducts(keyword, categorySlug, minPrice, maxPrice, featured);
    }

    @GetMapping("/search")
    public ProductCatalogResponse search(@RequestParam String keyword,
            @RequestParam(required = false) String categorySlug) {
        return storefrontService.filterActiveProducts(keyword, categorySlug, null, null, null);
    }

    @GetMapping("/products/{slug}")
    public com.example.cyan.catalog.model.Product productDetail(@PathVariable String slug) {
        return storefrontService.getActiveProductBySlug(slug);
    }

    @GetMapping("/products/{slug}/related")
    public List<ProductCardResponse> relatedProducts(@PathVariable String slug,
            @RequestParam(defaultValue = "4") int limit) {
        return storefrontService.getRelatedProducts(slug, limit);
    }

    @GetMapping("/editorials")
    public List<EditorialSummaryResponse> editorials(@RequestParam(required = false) String topic,
            @RequestParam(required = false) String keyword) {
        return storefrontService.getPublishedEditorials(topic, keyword);
    }

    @GetMapping("/editorials/{slug}")
    public Editorial editorialDetail(@PathVariable String slug) {
        return storefrontService.getPublishedEditorialBySlug(slug);
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@Valid @RequestBody Order order) {
        return storefrontService.createOrder(order);
    }

    @PostMapping("/orders/lookup")
    public Order lookupOrder(@Valid @RequestBody OrderLookupRequest request) {
        return storefrontService.lookupOrder(request.orderCode(), request.phoneNumber());
    }

    public record OrderLookupRequest(
            @NotBlank String orderCode,
            @NotBlank String phoneNumber) {
    }
}
