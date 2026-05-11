package com.example.cyan.content.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.cyan.common.model.enums.EditorialStatus;
import com.example.cyan.content.model.Editorial;

public interface EditorialRepository extends MongoRepository<Editorial, String> {

    Optional<Editorial> findBySlug(String slug);

    List<Editorial> findByStatus(EditorialStatus status);

    long countByStatus(EditorialStatus status);
}
