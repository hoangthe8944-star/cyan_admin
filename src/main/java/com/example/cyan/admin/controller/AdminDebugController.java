package com.example.cyan.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cyan.admin.dto.MongoDebugInfoResponse;
import com.example.cyan.common.config.MongoDebugService;

@RestController
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
