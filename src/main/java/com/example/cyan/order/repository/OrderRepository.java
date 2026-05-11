package com.example.cyan.order.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.cyan.common.model.enums.OrderStatus;
import com.example.cyan.common.model.enums.PaymentStatus;
import com.example.cyan.order.model.Order;

public interface OrderRepository extends MongoRepository<Order, String> {

    Optional<Order> findByOrderCode(String orderCode);

    long countByOrderStatus(OrderStatus orderStatus);

    long countByPaymentStatus(PaymentStatus paymentStatus);
}
