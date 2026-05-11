package com.example.cyan.content.service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.cyan.common.exception.ResourceNotFoundException;
import com.example.cyan.common.model.enums.EditorialStatus;
import com.example.cyan.common.util.SlugUtils;
import com.example.cyan.content.model.Editorial;
import com.example.cyan.content.repository.EditorialRepository;

@Service
public class EditorialService {

    private final EditorialRepository editorialRepository;

    public EditorialService(EditorialRepository editorialRepository) {
        this.editorialRepository = editorialRepository;
    }

    public Editorial create(Editorial editorial) {
        prepareEditorial(editorial);
        return editorialRepository.save(editorial);
    }

    public List<Editorial> findAll(EditorialStatus status) {
        List<Editorial> editorials = status == null ? editorialRepository.findAll() : editorialRepository.findByStatus(status);
        return editorials.stream()
                .sorted(Comparator.comparing(Editorial::getPublishedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    public Editorial findById(String id) {
        return editorialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Editorial not found: " + id));
    }

    public Editorial findBySlug(String slug) {
        return editorialRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Editorial not found with slug: " + slug));
    }

    public Editorial update(String id, Editorial editorial) {
        Editorial existing = findById(id);
        editorial.setId(existing.getId());
        editorial.setCreatedAt(existing.getCreatedAt());
        editorial.setVersion(existing.getVersion());
        prepareEditorial(editorial);
        return editorialRepository.save(editorial);
    }

    public void delete(String id) {
        findById(id);
        editorialRepository.deleteById(id);
    }

    private void prepareEditorial(Editorial editorial) {
        editorial.setSlug(resolveSlug(editorial.getSlug(), editorial.getTitle()));
        if (editorial.getStatus() == EditorialStatus.PUBLISHED && editorial.getPublishedAt() == null) {
            editorial.setPublishedAt(Instant.now());
        }
    }

    private String resolveSlug(String slug, String fallbackName) {
        String resolved = SlugUtils.toSlug(slug);
        return resolved != null ? resolved : SlugUtils.toSlug(fallbackName);
    }
}
