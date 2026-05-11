package com.example.cyan.admin.service;

import org.springframework.stereotype.Service;

import com.example.cyan.admin.dto.AdminDashboardResponse;
import com.example.cyan.catalog.repository.CategoryRepository;
import com.example.cyan.catalog.repository.ProductRepository;
import com.example.cyan.common.model.enums.CategoryStatus;
import com.example.cyan.common.model.enums.EditorialStatus;
import com.example.cyan.common.model.enums.PaymentStatus;
import com.example.cyan.common.model.enums.ProductStatus;
import com.example.cyan.content.repository.BannerRepository;
import com.example.cyan.content.repository.EditorialRepository;
import com.example.cyan.order.repository.OrderRepository;

@Service
public class AdminDashboardService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final BannerRepository bannerRepository;
    private final EditorialRepository editorialRepository;

    public AdminDashboardService(CategoryRepository categoryRepository, ProductRepository productRepository,
            OrderRepository orderRepository, BannerRepository bannerRepository, EditorialRepository editorialRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.bannerRepository = bannerRepository;
        this.editorialRepository = editorialRepository;
    }

    public AdminDashboardResponse getSummary() {
        return new AdminDashboardResponse(
                categoryRepository.count(),
                categoryRepository.countByStatus(CategoryStatus.ACTIVE),
                productRepository.count(),
                productRepository.countByStatus(ProductStatus.ACTIVE),
                orderRepository.count(),
                orderRepository.countByPaymentStatus(PaymentStatus.PAID),
                bannerRepository.count(),
                bannerRepository.countByActive(true),
                editorialRepository.count(),
                editorialRepository.countByStatus(EditorialStatus.PUBLISHED));
    }
}
