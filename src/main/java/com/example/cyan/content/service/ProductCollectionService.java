package com.example.cyan.content.service;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.cyan.catalog.repository.ProductRepository;
import com.example.cyan.common.exception.BadRequestException;
import com.example.cyan.common.exception.ResourceNotFoundException;
import com.example.cyan.common.model.enums.CollectionStatus;
import com.example.cyan.common.util.SlugUtils;
import com.example.cyan.content.model.ProductCollection;
import com.example.cyan.content.repository.ProductCollectionRepository;

@Service
public class ProductCollectionService {

    private final ProductCollectionRepository productCollectionRepository;
    private final ProductRepository productRepository;

    public ProductCollectionService(ProductCollectionRepository productCollectionRepository,
            ProductRepository productRepository) {
        this.productCollectionRepository = productCollectionRepository;
        this.productRepository = productRepository;
    }

    public ProductCollection create(ProductCollection collection) {
        prepareCollection(collection);
        return productCollectionRepository.save(collection);
    }

    public List<ProductCollection> findAll(CollectionStatus status) {
        List<ProductCollection> collections = status == null ? productCollectionRepository.findAll()
                : productCollectionRepository.findByStatus(status);
        return collections.stream()
                .sorted(Comparator.comparingInt(ProductCollection::getDisplayOrder)
                        .thenComparing(ProductCollection::getPublishedAt, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(ProductCollection::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    public ProductCollection findById(String id) {
        return productCollectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found: " + id));
    }

    public ProductCollection findBySlug(String slug) {
        return productCollectionRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found with slug: " + slug));
    }

    public ProductCollection update(String id, ProductCollection collection) {
        ProductCollection existing = findById(id);
        collection.setId(existing.getId());
        collection.setCreatedAt(existing.getCreatedAt());
        collection.setVersion(existing.getVersion());
        prepareCollection(collection);
        return productCollectionRepository.save(collection);
    }

    public void delete(String id) {
        findById(id);
        productCollectionRepository.deleteById(id);
    }

    private void prepareCollection(ProductCollection collection) {
        collection.setSlug(resolveSlug(collection.getSlug(), collection.getName()));

        List<String> productIds = collection.getProductIds() == null ? List.of() : collection.getProductIds().stream()
                .filter(id -> id != null && !id.isBlank())
                .map(String::trim)
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new))
                .stream()
                .toList();

        boolean invalidProduct = productIds.stream().anyMatch(productId -> productRepository.findById(productId).isEmpty());
        if (invalidProduct) {
            throw new BadRequestException("One or more productIds do not exist");
        }

        collection.setProductIds(productIds);
        if (collection.getStatus() == CollectionStatus.PUBLISHED && collection.getPublishedAt() == null) {
            collection.setPublishedAt(Instant.now());
        }
    }

    private String resolveSlug(String slug, String fallbackName) {
        String resolved = SlugUtils.toSlug(slug);
        if (resolved == null) {
            resolved = SlugUtils.toSlug(fallbackName);
        }
        if (resolved == null) {
            throw new BadRequestException("Slug or name is required");
        }
        return resolved;
    }
}
