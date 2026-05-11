package com.example.cyan.catalog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.cyan.catalog.model.Category;
import com.example.cyan.common.model.enums.CategoryStatus;

public interface CategoryRepository extends MongoRepository<Category, String> {

    Optional<Category> findBySlug(String slug);

    List<Category> findByStatusOrderByLevelAscDisplayOrderAscNameAsc(CategoryStatus status);

    List<Category> findByParentIdOrderByDisplayOrderAscNameAsc(String parentId);

    long countByStatus(CategoryStatus status);
}
