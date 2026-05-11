package com.example.cyan.catalog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.cyan.catalog.model.Product;
import com.example.cyan.common.model.enums.ProductStatus;

public interface ProductRepository extends MongoRepository<Product, String> {

    Optional<Product> findBySlug(String slug);

    List<Product> findByStatus(ProductStatus status);

    List<Product> findByCategoryIdsContaining(String categoryId);

    long countByStatus(ProductStatus status);
}
