package com.example.cyan.admin.controller;

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

import com.example.cyan.common.model.enums.CollectionStatus;
import com.example.cyan.content.model.ProductCollection;
import com.example.cyan.content.service.ProductCollectionService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/admin/collections")
public class AdminCollectionController {

    private final ProductCollectionService productCollectionService;

    public AdminCollectionController(ProductCollectionService productCollectionService) {
        this.productCollectionService = productCollectionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductCollection create(@Valid @RequestBody ProductCollection collection) {
        return productCollectionService.create(collection);
    }

    @GetMapping
    public List<ProductCollection> findAll(@RequestParam(required = false) CollectionStatus status) {
        return productCollectionService.findAll(status);
    }

    @GetMapping("/{id}")
    public ProductCollection findById(@PathVariable String id) {
        return productCollectionService.findById(id);
    }

    @GetMapping("/slug/{slug}")
    public ProductCollection findBySlug(@PathVariable String slug) {
        return productCollectionService.findBySlug(slug);
    }

    @PutMapping("/{id}")
    public ProductCollection update(@PathVariable String id, @Valid @RequestBody ProductCollection collection) {
        return productCollectionService.update(id, collection);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        productCollectionService.delete(id);
    }
}
