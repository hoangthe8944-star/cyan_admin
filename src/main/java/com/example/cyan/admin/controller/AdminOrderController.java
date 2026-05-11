package com.example.cyan.admin.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cyan.common.model.enums.OrderStatus;
import com.example.cyan.common.model.enums.PaymentStatus;
import com.example.cyan.order.model.Order;
import com.example.cyan.order.service.OrderService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Validated
@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public Order findById(@PathVariable String id) {
        return orderService.findById(id);
    }

    @GetMapping("/code/{orderCode}")
    public Order findByCode(@PathVariable String orderCode) {
        return orderService.findByCode(orderCode);
    }

    @PatchMapping("/{id}/status")
    public Order updateStatus(@PathVariable String id, @Valid @RequestBody UpdateOrderStatusRequest request) {
        return orderService.updateStatus(id, request.orderStatus(), request.paymentStatus());
    }

    @PatchMapping("/{id}/momo-callback")
    public Order updateMomoCallback(@PathVariable String id, @Valid @RequestBody UpdateMomoCallbackRequest request) {
        return orderService.updateMomoCallback(id, request.paymentStatus(), request.resultCode(), request.transId(),
                request.message(), request.payUrl(), request.deeplink(), request.qrCodeUrl());
    }

    public record UpdateOrderStatusRequest(
            @NotNull OrderStatus orderStatus,
            @NotNull PaymentStatus paymentStatus) {
    }

    public record UpdateMomoCallbackRequest(
            @NotNull PaymentStatus paymentStatus,
            Integer resultCode,
            Long transId,
            String message,
            String payUrl,
            String deeplink,
            String qrCodeUrl) {
    }
}
