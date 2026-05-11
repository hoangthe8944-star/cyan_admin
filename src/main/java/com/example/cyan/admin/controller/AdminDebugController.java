package com.example.cyan.admin.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cyan.admin.dto.MongoDebugInfoResponse;
import com.example.cyan.common.config.MongoDebugService;

@RestController
@CrossOrigin(origins = { "http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:4173",
        "http://127.0.0.1:4173", "http://localhost:3000", "http://127.0.0.1:3000" })
@RequestMapping("/api/admin/debug")
public class AdminDebugController {

    private final MongoDebugService mongoDebugService;

    public AdminDebugController(MongoDebugService mongoDebugService) {
        this.mongoDebugService = mongoDebugService;
    }

    @GetMapping("/mongo-info")
    public MongoDebugInfoResponse mongoInfo() {
        return mongoDebugService.getInfo();
    }
}
