package com.example.cyan.admin.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
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

import com.example.cyan.catalog.model.Product;
import com.example.cyan.catalog.service.ProductService;
import com.example.cyan.common.model.enums.ProductStatus;

import jakarta.validation.Valid;

@Validated
@RestController
@CrossOrigin(origins = { "http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:4173",
        "http://127.0.0.1:4173", "http://localhost:3000", "http://127.0.0.1:3000" })
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
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

    @GetMapping("/slug/{slug}")
    public Product findBySlug(@PathVariable String slug) {
        return productService.findBySlug(slug);
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
