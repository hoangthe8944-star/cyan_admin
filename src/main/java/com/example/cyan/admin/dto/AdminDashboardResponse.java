package com.example.cyan.admin.dto;

public record AdminDashboardResponse(
        long totalCategories,
        long activeCategories,
        long totalProducts,
        long activeProducts,
        long totalOrders,
        long paidOrders,
        long totalBanners,
        long activeBanners,
        long totalEditorials,
        long publishedEditorials) {
}
