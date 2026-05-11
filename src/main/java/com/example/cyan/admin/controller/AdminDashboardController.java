package com.example.cyan.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cyan.admin.dto.AdminDashboardResponse;
import com.example.cyan.admin.service.AdminDashboardService;

@RestController
@CrossOrigin(origins = { "http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:4173",
        "http://127.0.0.1:4173", "http://localhost:3000", "http://127.0.0.1:3000" })
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping
    public AdminDashboardResponse summary() {
        return adminDashboardService.getSummary();
    }
}
