package com.example.cyan.order.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.cyan.common.exception.BadRequestException;
import com.example.cyan.common.exception.ResourceNotFoundException;
import com.example.cyan.common.model.enums.OrderStatus;
import com.example.cyan.common.model.enums.PaymentMethod;
import com.example.cyan.common.model.enums.PaymentStatus;
import com.example.cyan.order.model.Order;
import com.example.cyan.order.model.OrderItem;
import com.example.cyan.order.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order create(Order order) {
        prepareOrder(order);
        return orderRepository.save(order);
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
    }

    public Order findByCode(String orderCode) {
        return orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with code: " + orderCode));
    }

    public Order updateStatus(String id, OrderStatus orderStatus, PaymentStatus paymentStatus) {
        Order order = findById(id);
        order.setOrderStatus(orderStatus);
        order.setPaymentStatus(paymentStatus);
        return orderRepository.save(order);
    }

    public Order updateMomoCallback(String id, PaymentStatus paymentStatus, Integer resultCode,
            Long transId, String message, String payUrl, String deeplink, String qrCodeUrl) {
        Order order = findById(id);
        if (order.getMomoPayment() == null) {
            throw new BadRequestException("This order does not contain MoMo payment metadata");
        }

        order.getMomoPayment().setResultCode(resultCode);
        order.getMomoPayment().setTransId(transId);
        order.getMomoPayment().setMessage(message);
        order.getMomoPayment().setPayUrl(payUrl);
        order.getMomoPayment().setDeeplink(deeplink);
        order.getMomoPayment().setQrCodeUrl(qrCodeUrl);
        order.getMomoPayment().setResponseTime(Instant.now());
        order.setPaymentStatus(paymentStatus);
        if (paymentStatus == PaymentStatus.PAID) {
            order.setOrderStatus(OrderStatus.PAID);
        }
        return orderRepository.save(order);
    }

    private void prepareOrder(Order order) {
        BigDecimal subtotal = order.getItems().stream()
                .map(this::normalizeLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal shippingFee = defaultValue(order.getShippingFee());
        BigDecimal discountAmount = defaultValue(order.getDiscountAmount());
        BigDecimal totalAmount = subtotal.add(shippingFee).subtract(discountAmount);
        if (totalAmount.signum() < 0) {
            throw new BadRequestException("Total amount cannot be negative");
        }

        order.setOrderCode(order.getOrderCode() == null || order.getOrderCode().isBlank()
                ? "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()
                : order.getOrderCode());
        order.setSubtotal(subtotal);
        order.setShippingFee(shippingFee);
        order.setDiscountAmount(discountAmount);
        order.setTotalAmount(totalAmount);

        if (order.getBillingAddress() == null) {
            order.setBillingAddress(order.getShippingAddress());
        }

        if (order.getPaymentMethod() == PaymentMethod.MOMO && order.getMomoPayment() != null) {
            order.getMomoPayment().setAmount(totalAmount.setScale(0, RoundingMode.HALF_UP).longValue());
            order.getMomoPayment().setOrderInfo(order.getMomoPayment().getOrderInfo() == null
                    ? "Thanh toan don hang " + order.getOrderCode()
                    : order.getMomoPayment().getOrderInfo());
            order.getMomoPayment().setMomoOrderId(order.getMomoPayment().getMomoOrderId() == null
                    ? order.getOrderCode()
                    : order.getMomoPayment().getMomoOrderId());
            order.getMomoPayment().setRequestId(order.getMomoPayment().getRequestId() == null
                    ? UUID.randomUUID().toString()
                    : order.getMomoPayment().getRequestId());
        }
    }

    private BigDecimal normalizeLineTotal(OrderItem item) {
        BigDecimal unitPrice = defaultValue(item.getUnitPrice());
        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
        item.setLineTotal(lineTotal);
        return lineTotal;
    }

    private BigDecimal defaultValue(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
