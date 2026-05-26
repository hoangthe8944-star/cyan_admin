package com.example.cyan.catalog.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.cyan.catalog.dto.ProductVariantStyleDetailResponse;
import com.example.cyan.catalog.model.Product;
import com.example.cyan.catalog.service.ProductService;
import com.example.cyan.common.model.enums.ProductStatus;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@Valid @RequestBody Product product) {
        return productService.create(product);
    }

    @GetMapping
    public List<Product> findAll(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) ProductStatus status) {
        return productService.findAll(keyword, categoryId, status);
    }

    @GetMapping("/{id}")
    public Product findById(@PathVariable String id) {
        return productService.findById(id);
    }

    @GetMapping("/{id}/styles/{styleCode}")
    public ProductVariantStyleDetailResponse findVariantByStyleCode(@PathVariable String id,
            @PathVariable String styleCode) {
        return productService.findVariantByStyleCode(id, styleCode);
    }

    @GetMapping("/slug/{slug}")
    public Product findBySlug(@PathVariable String slug) {
        return productService.findBySlug(slug);
    }

    @GetMapping("/slug/{slug}/styles/{styleCode}")
    public ProductVariantStyleDetailResponse findVariantByStyleCodeFromSlug(@PathVariable String slug,
            @PathVariable String styleCode) {
        return productService.findVariantByStyleCodeFromSlug(slug, styleCode);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable String id, @Valid @RequestBody Product product) {
        return productService.update(id, product);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        productService.delete(id);
    }
}
