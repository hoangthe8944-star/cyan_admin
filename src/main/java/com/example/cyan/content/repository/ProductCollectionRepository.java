package com.example.cyan.content.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.cyan.common.model.enums.CollectionStatus;
import com.example.cyan.content.model.ProductCollection;

public interface ProductCollectionRepository extends MongoRepository<ProductCollection, String> {

    Optional<ProductCollection> findBySlug(String slug);

    List<ProductCollection> findByStatus(CollectionStatus status);
}
