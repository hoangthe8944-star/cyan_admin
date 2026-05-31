package com.example.cyan.content.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.cyan.content.model.LandingPageTheme;

public interface LandingPageThemeRepository extends MongoRepository<LandingPageTheme, String> {

    Optional<LandingPageTheme> findBySlug(String slug);

    Optional<LandingPageTheme> findFirstByActiveTrue();

    List<LandingPageTheme> findByActiveTrue();
}
