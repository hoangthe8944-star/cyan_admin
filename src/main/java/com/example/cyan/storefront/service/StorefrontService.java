package com.example.cyan.storefront.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.example.cyan.catalog.model.Category;
import com.example.cyan.catalog.model.Product;
import com.example.cyan.catalog.service.CategoryService;
import com.example.cyan.catalog.service.ProductService;
import com.example.cyan.common.exception.ResourceNotFoundException;
import com.example.cyan.common.model.enums.BannerPlacement;
import com.example.cyan.common.model.enums.CategoryStatus;
import com.example.cyan.common.model.enums.EditorialStatus;
import com.example.cyan.common.model.enums.ProductStatus;
import com.example.cyan.content.model.Banner;
import com.example.cyan.content.model.Editorial;
import com.example.cyan.content.service.BannerService;
import com.example.cyan.content.service.EditorialService;
import com.example.cyan.order.model.Order;
import com.example.cyan.order.service.OrderService;
import com.example.cyan.storefront.dto.CategoryTreeResponse;
import com.example.cyan.storefront.dto.HomeResponse;
import com.example.cyan.storefront.dto.ProductCatalogResponse;
import com.example.cyan.storefront.dto.ProductCardResponse;

@Service
public class StorefrontService {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final BannerService bannerService;
    private final EditorialService editorialService;
    private final OrderService orderService;
    private final StorefrontMapper storefrontMapper;

    public StorefrontService(CategoryService categoryService, ProductService productService, BannerService bannerService,
            EditorialService editorialService, OrderService orderService, StorefrontMapper storefrontMapper) {
        this.categoryService = categoryService;
        this.productService = productService;
        this.bannerService = bannerService;
        this.editorialService = editorialService;
        this.orderService = orderService;
        this.storefrontMapper = storefrontMapper;
    }

    public HomeResponse getHome() {
        HomeResponse response = new HomeResponse();
        response.setMainBanners(activeBanners(BannerPlacement.MAIN));
        response.setSubBanners(activeBanners(BannerPlacement.SUB));
        response.setCategories(getCategoryTree());
        response.setFeaturedProducts(filterActiveProducts(null, null, null, null, true).getItems().stream().limit(8).toList());
        response.setNewArrivals(getNewArrivals(8));
        response.setLatestEditorials(editorialService.findAll(EditorialStatus.PUBLISHED).stream()
                .limit(6)
                .map(storefrontMapper::toEditorialSummary)
                .toList());
        return response;
    }

    public List<Banner> activeBanners(BannerPlacement placement) {
        Instant now = Instant.now();
        return bannerService.findActiveByPlacement(placement).stream()
                .filter(banner -> banner.getStartsAt() == null || !banner.getStartsAt().isAfter(now))
                .filter(banner -> banner.getEndsAt() == null || !banner.getEndsAt().isBefore(now))
                .toList();
    }

    public List<CategoryTreeResponse> getCategoryTree() {
        List<Category> categories = categoryService.findActiveTree();
        Map<String, CategoryTreeResponse> nodes = new LinkedHashMap<>();
        List<CategoryTreeResponse> roots = new ArrayList<>();

        for (Category category : categories) {
            nodes.put(category.getId(), storefrontMapper.toCategoryTree(category));
        }

        for (Category category : categories) {
            CategoryTreeResponse node = nodes.get(category.getId());
            if (category.getParentId() == null) {
                roots.add(node);
            } else {
                CategoryTreeResponse parent = nodes.get(category.getParentId());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            }
        }

        return roots;
    }

    public ProductCatalogResponse filterActiveProducts(String keyword, String categorySlug, BigDecimal minPrice,
            BigDecimal maxPrice, Boolean featured) {
        String categoryId = resolveCategoryId(categorySlug);
        List<ProductCardResponse> items = productService.findAll(keyword, categoryId, ProductStatus.ACTIVE).stream()
                .filter(product -> minPrice == null || product.getMaxPrice().compareTo(minPrice) >= 0)
                .filter(product -> maxPrice == null || product.getMinPrice().compareTo(maxPrice) <= 0)
                .filter(product -> featured == null || product.isFeatured() == featured.booleanValue())
                .map(storefrontMapper::toProductCard)
                .toList();

        ProductCatalogResponse response = new ProductCatalogResponse();
        response.setTotal(items.size());
        response.setItems(items);
        return response;
    }

    public CategoryTreeResponse getCategoryBySlug(String slug) {
        Category category = categoryService.findBySlug(slug);
        if (category.getStatus() != CategoryStatus.ACTIVE) {
            throw new ResourceNotFoundException("Category not found with slug: " + slug);
        }
        return storefrontMapper.toCategoryTree(category);
    }

    public Product getActiveProductBySlug(String slug) {
        Product product = productService.findBySlug(slug);
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new ResourceNotFoundException("Product not found with slug: " + slug);
        }
        return product;
    }

    public List<ProductCardResponse> getRelatedProducts(String slug, int limit) {
        Product product = getActiveProductBySlug(slug);
        return productService.findAll(null, product.getPrimaryCategoryId(), ProductStatus.ACTIVE).stream()
                .filter(item -> !Objects.equals(item.getId(), product.getId()))
                .sorted(Comparator.comparing(Product::isFeatured).reversed()
                        .thenComparing(Product::getPublishedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .map(storefrontMapper::toProductCard)
                .toList();
    }

    public List<ProductCardResponse> getNewArrivals(int limit) {
        return productService.findAll(null, null, ProductStatus.ACTIVE).stream()
                .sorted(Comparator.comparing(Product::getPublishedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .map(storefrontMapper::toProductCard)
                .toList();
    }

    public List<com.example.cyan.storefront.dto.EditorialSummaryResponse> getPublishedEditorials(String topic,
            String keyword) {
        String normalizedTopic = topic == null ? null : topic.toLowerCase(Locale.ROOT);
        String normalizedKeyword = keyword == null ? null : keyword.toLowerCase(Locale.ROOT);
        return editorialService.findAll(EditorialStatus.PUBLISHED).stream()
                .filter(editorial -> normalizedTopic == null || editorial.getTopics().stream()
                        .filter(Objects::nonNull)
                        .map(value -> value.toLowerCase(Locale.ROOT))
                        .anyMatch(value -> value.equals(normalizedTopic)))
                .filter(editorial -> normalizedKeyword == null
                        || containsIgnoreCase(editorial.getTitle(), normalizedKeyword)
                        || containsIgnoreCase(editorial.getSummary(), normalizedKeyword))
                .map(storefrontMapper::toEditorialSummary)
                .toList();
    }

    public Editorial getPublishedEditorialBySlug(String slug) {
        Editorial editorial = editorialService.findBySlug(slug);
        if (editorial.getStatus() != EditorialStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Editorial not found with slug: " + slug);
        }
        return editorial;
    }

    public Order createOrder(Order order) {
        return orderService.create(order);
    }

    public Order lookupOrder(String orderCode, String phoneNumber) {
        Order order = orderService.findByCode(orderCode);
        if (order.getCustomer() == null || order.getCustomer().getPhoneNumber() == null
                || !order.getCustomer().getPhoneNumber().equals(phoneNumber)) {
            throw new ResourceNotFoundException("Order not found for the provided credentials");
        }
        return order;
    }

    private boolean containsIgnoreCase(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private String resolveCategoryId(String categorySlug) {
        if (categorySlug == null || categorySlug.isBlank()) {
            return null;
        }
        return categoryService.findAll().stream()
                .filter(category -> categorySlug.equals(category.getSlug()))
                .map(Category::getId)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + categorySlug));
    }
}
