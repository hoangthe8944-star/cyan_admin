package com.example.cyan.catalog.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.example.cyan.catalog.model.Product;
import com.example.cyan.catalog.model.ProductVariant;
import com.example.cyan.catalog.repository.CategoryRepository;
import com.example.cyan.catalog.repository.ProductRepository;
import com.example.cyan.common.exception.BadRequestException;
import com.example.cyan.common.exception.ResourceNotFoundException;
import com.example.cyan.common.model.enums.ProductOptionType;
import com.example.cyan.common.model.enums.ProductStatus;
import com.example.cyan.common.util.SlugUtils;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Product create(Product product) {
        prepareProduct(product);
        return productRepository.save(product);
    }

    public List<Product> findAll(String keyword, String categoryId, ProductStatus status) {
        List<Product> products = status != null ? productRepository.findByStatus(status) : productRepository.findAll();
        return products.stream()
                .filter(product -> keyword == null || keyword.isBlank()
                        || product.getName().toLowerCase().contains(keyword.toLowerCase())
                        || product.getSku().toLowerCase().contains(keyword.toLowerCase()))
                .filter(product -> categoryId == null || categoryId.isBlank()
                        || product.getCategoryIds().contains(categoryId))
                .sorted(Comparator.comparing(Product::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    public Product findById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    public Product findBySlug(String slug) {
        return productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with slug: " + slug));
    }

    public Product update(String id, Product product) {
        Product existing = findById(id);
        product.setId(existing.getId());
        product.setCreatedAt(existing.getCreatedAt());
        product.setVersion(existing.getVersion());
        prepareProduct(product);
        return productRepository.save(product);
    }

    public void delete(String id) {
        findById(id);
        productRepository.deleteById(id);
    }

    private void prepareProduct(Product product) {
        if (categoryRepository.findById(product.getPrimaryCategoryId()).isEmpty()) {
            throw new BadRequestException("Primary category does not exist");
        }
        boolean invalidCategory = product.getCategoryIds().stream().anyMatch(categoryId -> categoryRepository.findById(categoryId).isEmpty());
        if (invalidCategory) {
            throw new BadRequestException("One or more categoryIds do not exist");
        }

        boolean hasModelOption = product.getOptions().stream().anyMatch(option -> option.getType() == ProductOptionType.MODEL);
        boolean hasStyleOption = product.getOptions().stream().anyMatch(option -> option.getType() == ProductOptionType.STYLE);
        if (!hasModelOption || !hasStyleOption) {
            throw new BadRequestException("Product must support both MODEL and STYLE options");
        }

        if (product.getVariants().stream().map(ProductVariant::getVariantCode).anyMatch(Objects::isNull)) {
            throw new BadRequestException("Each variant must have a variantCode");
        }

        product.setSlug(resolveSlug(product.getSlug(), product.getName()));

        BigDecimal minPrice = product.getVariants().stream()
                .map(ProductVariant::getPrice)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
        BigDecimal maxPrice = product.getVariants().stream()
                .map(ProductVariant::getPrice)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
        int totalStock = product.getVariants().stream()
                .mapToInt(ProductVariant::getStockQuantity)
                .sum();

        product.setMinPrice(minPrice);
        product.setMaxPrice(maxPrice);
        product.setTotalStock(totalStock);

        if (product.getStatus() == ProductStatus.ACTIVE && product.getPublishedAt() == null) {
            product.setPublishedAt(Instant.now());
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
