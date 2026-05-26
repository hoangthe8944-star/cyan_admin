package com.example.cyan.catalog.service;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.Instant;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return attachProductDetailsToVariants(productRepository.save(product));
    }

    public List<Product> findAll(String keyword, String categoryId, ProductStatus status) {
        List<Product> products = status != null ? productRepository.findByStatus(status) : productRepository.findAll();
        return products.stream()
                .filter(product -> matchesKeyword(product, keyword))
                .filter(product -> categoryId == null || categoryId.isBlank()
                        || product.getCategoryIds().contains(categoryId))
                .sorted(Comparator.comparing(Product::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::attachProductDetailsToVariants)
                .toList();
    }

    public Product findActiveById(String id) {
        Product product = findById(id);
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new ResourceNotFoundException("Product is not active: " + id);
        }
        return product;
    }

    public ProductVariant findVariant(Product product, String variantCode) {
        return product.getVariants().stream()
                .filter(ProductVariant::isActive)
                .filter(variant -> variantCode.equals(variant.getVariantCode()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found: " + variantCode));
    }

    public List<String> suggestKeywords(String keyword, int limit) {
        String normalizedKeyword = normalizeKeyword(keyword);
        if (normalizedKeyword == null) {
            return List.of();
        }

        return findAll(null, null, ProductStatus.ACTIVE).stream()
                .flatMap(this::keywordCandidates)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .filter(value -> {
                    String normalizedValue = normalizeKeyword(value);
                    return normalizedValue != null && normalizedValue.contains(normalizedKeyword);
                })
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .stream()
                .sorted(Comparator.<String>comparingInt(
                        value -> suggestionScore(value, normalizedKeyword))
                        .thenComparingInt(String::length)
                        .thenComparing(String.CASE_INSENSITIVE_ORDER))
                .limit(limit)
                .toList();
    }

    public List<Product> suggestProducts(String keyword, int limit) {
        String normalizedKeyword = normalizeKeyword(keyword);
        if (normalizedKeyword == null) {
            return List.of();
        }

        return findAll(null, null, ProductStatus.ACTIVE).stream()
                .filter(product -> matchesKeyword(product, keyword))
                .sorted(Comparator
                        .comparingInt((Product product) -> suggestionScore(product.getName(), normalizedKeyword))
                        .thenComparing(Product::isFeatured, Comparator.reverseOrder())
                        .thenComparing(Product::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .toList();
    }

    public Product findById(String id) {
        return productRepository.findById(id)
                .map(this::attachProductDetailsToVariants)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    public Product findBySlug(String slug) {
        return productRepository.findBySlug(slug)
                .map(this::attachProductDetailsToVariants)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with slug: " + slug));
    }

    public Product update(String id, Product product) {
        Product existing = findById(id);
        product.setId(existing.getId());
        product.setCreatedAt(existing.getCreatedAt());
        product.setVersion(existing.getVersion());
        prepareProduct(product);
        return attachProductDetailsToVariants(productRepository.save(product));
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

        product.getVariants().forEach(variant -> prepareVariant(product.getName(), product.getDescription(), variant));

        boolean duplicateVariantCode = product.getVariants().stream()
                .map(ProductVariant::getVariantCode)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(code -> code, Collectors.counting()))
                .values()
                .stream()
                .anyMatch(count -> count > 1);
        if (duplicateVariantCode) {
            throw new BadRequestException("Generated variantCode must be unique");
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

    private void prepareVariant(String productName, String fullDescription, ProductVariant variant) {
        if (variant == null) {
            throw new BadRequestException("Variant is required");
        }

        variant.setProductName(productName);
        variant.setFullDescription(fullDescription);
        String generatedVariantCode = generateVariantCode(variant);
        if (generatedVariantCode == null) {
            throw new BadRequestException("Each variant must provide modelCode and styleCode");
        }
        variant.setVariantCode(generatedVariantCode);
    }

    private Product attachProductDetailsToVariants(Product product) {
        if (product == null || product.getVariants() == null) {
            return product;
        }
        product.getVariants().forEach(variant -> {
            if (variant != null) {
                variant.setProductName(product.getName());
                variant.setFullDescription(product.getDescription());
            }
        });
        return product;
    }

    private String generateVariantCode(ProductVariant variant) {
        String modelCode = normalizeVariantToken(variant.getModelCode());
        String styleCode = normalizeVariantToken(variant.getStyleCode());
        if (modelCode == null || styleCode == null) {
            return null;
        }

        String selectionSegment = variant.getSelections() == null ? null : variant.getSelections().stream()
                .filter(Objects::nonNull)
                .map(selection -> normalizeVariantToken(selection.getValueCode()))
                .filter(Objects::nonNull)
                .collect(Collectors.joining("-"));

        String baseCode = Stream.of(modelCode, styleCode, selectionSegment)
                .filter(Objects::nonNull)
                .filter(value -> !value.isBlank())
                .collect(Collectors.joining("-"));

        if (baseCode.length() <= 180) {
            return baseCode;
        }

        String hash = HexFormat.of().toHexDigits(baseCode.hashCode()).toUpperCase(Locale.ROOT);
        int maxBaseLength = 180 - hash.length() - 1;
        return baseCode.substring(0, Math.max(maxBaseLength, 1)) + "-" + hash;
    }

    private String normalizeVariantToken(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim()
                .replaceAll("\\s+", "-")
                .replaceAll("[^A-Za-z0-9_-]+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("(^-|-$)", "")
                .toUpperCase(Locale.ROOT);
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

    private boolean matchesKeyword(Product product, String keyword) {
        String normalizedKeyword = normalizeKeyword(keyword);
        if (normalizedKeyword == null) {
            return true;
        }

        return keywordCandidates(product)
                .filter(Objects::nonNull)
                .map(this::normalizeKeyword)
                .filter(Objects::nonNull)
                .anyMatch(value -> value.contains(normalizedKeyword));
    }

    private Stream<String> keywordCandidates(Product product) {
        Stream<String> tags = product.getTags() == null ? Stream.empty() : product.getTags().stream();
        return Stream.concat(
                Stream.of(product.getName(), product.getSku(), product.getBrand(), product.getMaterial(),
                        product.getGemstone(), product.getShortDescription()),
                tags);
    }

    private int suggestionScore(String value, String normalizedKeyword) {
        String normalizedValue = normalizeKeyword(value);
        if (normalizedValue == null) {
            return Integer.MAX_VALUE;
        }
        if (normalizedValue.equals(normalizedKeyword)) {
            return 0;
        }
        if (normalizedValue.startsWith(normalizedKeyword)) {
            return 1;
        }
        return 2;
    }

    private String normalizeKeyword(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ")
                .trim()
                .replaceAll("\\s+", " ");
        return normalized.isBlank() ? null : normalized;
    }
}
