package com.example.cyan.admin.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.cyan.common.model.enums.EditorialStatus;
import com.example.cyan.content.model.Editorial;
import com.example.cyan.content.service.EditorialService;

import jakarta.validation.Valid;

@Validated
@RestController
@CrossOrigin(origins = { "http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:4173",
        "http://127.0.0.1:4173", "http://localhost:3000", "http://127.0.0.1:3000" })
@RequestMapping("/api/admin/editorials")
public class AdminEditorialController {

    private final EditorialService editorialService;

    public AdminEditorialController(EditorialService editorialService) {
        this.editorialService = editorialService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Editorial create(@Valid @RequestBody Editorial editorial) {
        return editorialService.create(editorial);
    }

    @GetMapping
    public List<Editorial> findAll(@RequestParam(required = false) EditorialStatus status) {
        return editorialService.findAll(status);
    }

    @GetMapping("/{id}")
    public Editorial findById(@PathVariable String id) {
        return editorialService.findById(id);
    }

    @GetMapping("/slug/{slug}")
    public Editorial findBySlug(@PathVariable String slug) {
        return editorialService.findBySlug(slug);
    }

    @PutMapping("/{id}")
    public Editorial update(@PathVariable String id, @Valid @RequestBody Editorial editorial) {
        return editorialService.update(id, editorial);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        editorialService.delete(id);
    }
}
