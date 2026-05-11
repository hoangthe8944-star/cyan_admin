package com.example.cyan.catalog.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.cyan.catalog.model.Category;
import com.example.cyan.catalog.repository.CategoryRepository;
import com.example.cyan.common.exception.BadRequestException;
import com.example.cyan.common.exception.ResourceNotFoundException;
import com.example.cyan.common.model.enums.CategoryStatus;
import com.example.cyan.common.util.SlugUtils;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category create(Category category) {
        prepareCategory(category);
        return categoryRepository.save(category);
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public List<Category> findActiveTree() {
        return categoryRepository.findByStatusOrderByLevelAscDisplayOrderAscNameAsc(CategoryStatus.ACTIVE);
    }

    public Category findById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }

    public Category findBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug));
    }

    public Category update(String id, Category category) {
        Category existing = findById(id);
        category.setId(existing.getId());
        category.setCreatedAt(existing.getCreatedAt());
        category.setVersion(existing.getVersion());
        prepareCategory(category);
        return categoryRepository.save(category);
    }

    public void delete(String id) {
        findById(id);
        if (!categoryRepository.findByParentIdOrderByDisplayOrderAscNameAsc(id).isEmpty()) {
            throw new BadRequestException("Cannot delete category that still has child categories");
        }
        categoryRepository.deleteById(id);
    }

    private void prepareCategory(Category category) {
        category.setSlug(resolveSlug(category.getSlug(), category.getName()));
        if (category.getParentId() == null || category.getParentId().isBlank()) {
            category.setParentId(null);
            category.setRootCategoryId(null);
            category.setLevel(1);
            return;
        }

        Category parent = categoryRepository.findById(category.getParentId())
                .orElseThrow(() -> new BadRequestException("Parent category does not exist"));
        if (parent.getLevel() != 1) {
            throw new BadRequestException("Only 2 category levels are supported");
        }

        category.setLevel(2);
        category.setRootCategoryId(parent.getId());
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
