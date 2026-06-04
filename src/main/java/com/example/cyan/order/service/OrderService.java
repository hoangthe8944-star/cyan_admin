package com.example.cyan.order.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cyan.catalog.model.Product;
import com.example.cyan.catalog.model.ProductVariant;
import com.example.cyan.catalog.repository.ProductRepository;
import com.example.cyan.catalog.service.ProductService;
import com.example.cyan.common.exception.BadRequestException;
import com.example.cyan.common.exception.ResourceNotFoundException;
import com.example.cyan.common.model.enums.OrderStatus;
import com.example.cyan.common.model.enums.PaymentMethod;
import com.example.cyan.common.model.enums.PaymentStatus;
import com.example.cyan.order.dto.CheckoutOrderResponse;
import com.example.cyan.order.dto.CreateOrderRequest;
import com.example.cyan.order.dto.MomoIpnRequest;
import com.example.cyan.order.dto.CheckoutVnPayRequest;
import com.example.cyan.order.model.MomoPaymentInfo;
import com.example.cyan.order.model.VnPayPaymentInfo;
import com.example.cyan.order.model.Order;
import com.example.cyan.order.model.OrderItem;
import com.example.cyan.order.repository.OrderRepository;
import com.example.cyan.user.dto.UserResponse;
import com.example.cyan.user.model.User;
import com.example.cyan.user.repository.UserRepository;
import java.util.Objects;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final MomoPaymentService momoPaymentService;
    private final VnPayPaymentService vnPayPaymentService;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, ProductService productService,
            ProductRepository productRepository, MomoPaymentService momoPaymentService,
            VnPayPaymentService vnPayPaymentService, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.productRepository = productRepository;
        this.momoPaymentService = momoPaymentService;
        this.vnPayPaymentService = vnPayPaymentService;
        this.userRepository = userRepository;
    }

    public Order create(Order order) {
        prepareOrder(order);
        Order saved = orderRepository.save(order);
        populateUser(saved);
        return saved;
    }

    public List<Order> findAll() {
        List<Order> orders = orderRepository.findAll();
        populateUsers(orders);
        return orders;
    }

    public Order findById(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        populateUser(order);
        return order;
    }

    public Order findByCode(String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with code: " + orderCode));
        populateUser(order);
        return order;
    }

    public List<Order> findByUserId(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        populateUsers(orders);
        return orders;
    }

    public Order updateStatus(String id, OrderStatus orderStatus, PaymentStatus paymentStatus) {
        Order order = findById(id);
        order.setOrderStatus(orderStatus);
        order.setPaymentStatus(paymentStatus);
        Order saved = orderRepository.save(order);
        populateUser(saved);
        return saved;
    }

    public Order updateMomoCallback(String id, PaymentStatus paymentStatus, Integer resultCode,
            Long transId, String message, String payUrl, String deeplink, String qrCodeUrl) {
        Order order = findById(id);
        if (order.getMomoPayment() == null) {
            throw new BadRequestException("This order does not contain MoMo payment metadata");
        }

        maybeRestoreStockForFailedMomo(order, paymentStatus);

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
        Order saved = orderRepository.save(order);
        populateUser(saved);
        return saved;
    }

    public Order updateVnPayCallback(String id, PaymentStatus paymentStatus, String responseCode,
            String transactionNo, String bankCode, String payDate, String message) {
        Order order = findById(id);
        if (order.getVnpayPayment() == null) {
            throw new BadRequestException("This order does not contain VNPay payment metadata");
        }

        maybeRestoreStockForFailedVnPay(order, paymentStatus);

        order.getVnpayPayment().setResponseCode(responseCode);
        order.getVnpayPayment().setTransactionNo(transactionNo);
        if (bankCode != null) {
            order.getVnpayPayment().setBankCode(bankCode);
        }
        order.getVnpayPayment().setPayDate(payDate);
        order.getVnpayPayment().setMessage(message);
        order.getVnpayPayment().setResponseTime(Instant.now());
        order.setPaymentStatus(paymentStatus);
        if (paymentStatus == PaymentStatus.PAID) {
            order.setOrderStatus(OrderStatus.PAID);
        }
        Order saved = orderRepository.save(order);
        populateUser(saved);
        return saved;
    }

    @Transactional
    public CheckoutOrderResponse checkout(CreateOrderRequest request) {
        Map<String, Integer> requestedQuantityByVariant = new LinkedHashMap<>();
        request.getItems().forEach(item -> requestedQuantityByVariant.merge(item.getProductId() + "::" + item.getVariantCode(),
                item.getQuantity(), Integer::sum));

        Order order = new Order();
        order.setCustomer(request.getCustomer());
        order.setShippingAddress(request.getShippingAddress());
        order.setBillingAddress(request.getBillingAddress());
        order.setShippingFee(request.getShippingFee());
        order.setDiscountAmount(request.getDiscountAmount());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setNote(request.getNote());
        order.setPaymentStatus((request.getPaymentMethod() == PaymentMethod.MOMO || request.getPaymentMethod() == PaymentMethod.VNPAY) ? PaymentStatus.PENDING : PaymentStatus.UNPAID);
        order.setOrderStatus(OrderStatus.PENDING);
        if (request.getPaymentMethod() == PaymentMethod.MOMO) {
            order.setMomoPayment(new MomoPaymentInfo());
            if (request.getMomoPayment() != null) {
                order.getMomoPayment().setOrderInfo(request.getMomoPayment().getOrderInfo());
                order.getMomoPayment().setRedirectUrl(request.getMomoPayment().getRedirectUrl());
                order.getMomoPayment().setIpnUrl(request.getMomoPayment().getIpnUrl());
                order.getMomoPayment().setExtraData(request.getMomoPayment().getExtraData());
                order.getMomoPayment().setRequestType(request.getMomoPayment().getRequestType());
                order.getMomoPayment().setLang(request.getMomoPayment().getLang());
            }
        }
        if (request.getPaymentMethod() == PaymentMethod.VNPAY) {
            order.setVnpayPayment(new VnPayPaymentInfo());
            if (request.getVnpayPayment() != null) {
                order.getVnpayPayment().setOrderInfo(request.getVnpayPayment().getOrderInfo());
            }
        }

        for (var requestItem : request.getItems()) {
            Product product = productService.findActiveById(requestItem.getProductId());
            ProductVariant variant = productService.findVariant(product, requestItem.getVariantCode());
            int totalRequested = requestedQuantityByVariant.get(requestItem.getProductId() + "::" + requestItem.getVariantCode());
            if (variant.getStockQuantity() < totalRequested) {
                throw new BadRequestException("Insufficient stock for variant: " + requestItem.getVariantCode());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setVariantCode(variant.getVariantCode());
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(requestItem.getQuantity());
            orderItem.setUnitPrice(variant.getPrice());
            orderItem.setThumbnailUrl(resolveThumbnailUrl(product, variant));
            order.getItems().add(orderItem);
        }

        prepareOrder(order);

        Map<String, Product> touchedProducts = new LinkedHashMap<>();
        request.getItems().forEach(item -> {
            Product product = touchedProducts.computeIfAbsent(item.getProductId(), productService::findActiveById);
            ProductVariant variant = productService.findVariant(product, item.getVariantCode());
            variant.setStockQuantity(variant.getStockQuantity() - item.getQuantity());
            product.setTotalStock(product.getVariants().stream().mapToInt(ProductVariant::getStockQuantity).sum());
        });

        touchedProducts.values().forEach(productRepository::save);
        Order savedOrder = orderRepository.save(order);

        CheckoutOrderResponse response = new CheckoutOrderResponse();
        response.setPaymentRequired(savedOrder.getPaymentMethod() == PaymentMethod.MOMO || savedOrder.getPaymentMethod() == PaymentMethod.VNPAY);

        if (savedOrder.getPaymentMethod() == PaymentMethod.MOMO) {
            try {
                MomoPaymentService.MomoCreatePaymentResponse momoResponse = momoPaymentService.createPayment(savedOrder,
                        request.getMomoPayment());
                savedOrder.getMomoPayment().setPayUrl(momoResponse.payUrl());
                savedOrder.getMomoPayment().setDeeplink(momoResponse.deeplink());
                savedOrder.getMomoPayment().setQrCodeUrl(momoResponse.qrCodeUrl());
                savedOrder.getMomoPayment().setMessage(momoResponse.message());
                savedOrder.getMomoPayment().setResultCode(momoResponse.resultCode());
                savedOrder.getMomoPayment().setResponseTime(momoResponse.responseTime() == null ? null
                        : Instant.ofEpochMilli(momoResponse.responseTime()));
                savedOrder = orderRepository.save(savedOrder);
                response.setPayUrl(momoResponse.payUrl());
                response.setDeeplink(momoResponse.deeplink());
                response.setQrCodeUrl(momoResponse.qrCodeUrl());
            } catch (RuntimeException ex) {
                restoreStock(order);
                savedOrder.setPaymentStatus(PaymentStatus.FAILED);
                if (savedOrder.getMomoPayment() != null) {
                    savedOrder.getMomoPayment().setMessage(ex.getMessage());
                    savedOrder.getMomoPayment().setResponseTime(Instant.now());
                }
                orderRepository.save(savedOrder);
                populateUser(savedOrder);
                response.setOrder(savedOrder);
                throw ex;
            }
        } else if (savedOrder.getPaymentMethod() == PaymentMethod.VNPAY) {
            try {
                VnPayPaymentService.VnPayCreatePaymentResponse vnpayResponse = vnPayPaymentService.createPayment(savedOrder,
                        request.getVnpayPayment());
                savedOrder.getVnpayPayment().setPayUrl(vnpayResponse.payUrl());
                savedOrder = orderRepository.save(savedOrder);
                response.setPayUrl(vnpayResponse.payUrl());
            } catch (RuntimeException ex) {
                restoreStock(order);
                savedOrder.setPaymentStatus(PaymentStatus.FAILED);
                if (savedOrder.getVnpayPayment() != null) {
                    savedOrder.getVnpayPayment().setMessage(ex.getMessage());
                    savedOrder.getVnpayPayment().setResponseTime(Instant.now());
                }
                orderRepository.save(savedOrder);
                populateUser(savedOrder);
                response.setOrder(savedOrder);
                throw ex;
            }
        }

        populateUser(savedOrder);
        response.setOrder(savedOrder);
        return response;
    }

    @Transactional
    public void handleMomoIpn(MomoIpnRequest request) {
        momoPaymentService.verifyIpn(request);
        Order order = findByCode(request.getOrderId());
        if (order.getMomoPayment() == null) {
            throw new BadRequestException("This order does not contain MoMo payment metadata");
        }
        if (!request.getPartnerCode().equals(order.getMomoPayment().getPartnerCode())) {
            throw new BadRequestException("MoMo partner code mismatch");
        }
        if (!request.getRequestId().equals(order.getMomoPayment().getRequestId())) {
            throw new BadRequestException("MoMo requestId mismatch");
        }
        if (!request.getAmount().equals(order.getMomoPayment().getAmount())) {
            throw new BadRequestException("MoMo amount mismatch");
        }

        PaymentStatus nextPaymentStatus = resolvePaymentStatus(request.getResultCode());
        maybeRestoreStockForFailedMomo(order, nextPaymentStatus);
        order.getMomoPayment().setMessage(request.getMessage());
        order.getMomoPayment().setTransId(request.getTransId());
        order.getMomoPayment().setResultCode(request.getResultCode());
        order.getMomoPayment().setResponseTime(Instant.ofEpochMilli(request.getResponseTime()));
        order.setPaymentStatus(nextPaymentStatus);
        if (request.getResultCode() == 0) {
            order.setOrderStatus(OrderStatus.PAID);
        }
        orderRepository.save(order);
    }

    private void prepareOrder(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new BadRequestException("Order must contain at least one item");
        }
        BigDecimal subtotal = order.getItems().stream()
                .map(this::normalizeLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal shippingFee = defaultValue(order.getShippingFee());
        BigDecimal discountAmount = defaultValue(order.getDiscountAmount());
        BigDecimal totalAmount = subtotal.add(shippingFee).subtract(discountAmount);
        if (totalAmount.signum() < 0) {
            throw new BadRequestException("Total amount cannot be negative");
        }

        if (order.getCustomer() != null) {
            if (order.getUserId() == null || order.getUserId().isBlank()) {
                if (order.getCustomer().getUserId() != null && !order.getCustomer().getUserId().isBlank()) {
                    order.setUserId(order.getCustomer().getUserId());
                } else if (order.getCustomer().getEmail() != null) {
                    userRepository.findByEmailIgnoreCase(order.getCustomer().getEmail())
                            .ifPresent(user -> {
                                order.setUserId(user.getId());
                                order.getCustomer().setUserId(user.getId());
                            });
                }
            } else if (order.getCustomer().getUserId() == null || order.getCustomer().getUserId().isBlank()) {
                order.getCustomer().setUserId(order.getUserId());
            }
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
                    ? "Thanh toan don hang OrivenJewelry " + order.getOrderCode()
                    : order.getMomoPayment().getOrderInfo());
            order.getMomoPayment().setMomoOrderId(order.getMomoPayment().getMomoOrderId() == null
                    ? order.getOrderCode()
                    : order.getMomoPayment().getMomoOrderId());
            order.getMomoPayment().setRequestId(order.getMomoPayment().getRequestId() == null
                    ? UUID.randomUUID().toString()
                    : order.getMomoPayment().getRequestId());
        }
        if (order.getPaymentMethod() == PaymentMethod.VNPAY && order.getVnpayPayment() != null) {
            order.getVnpayPayment().setAmount(totalAmount.setScale(0, RoundingMode.HALF_UP).longValue());
            order.getVnpayPayment().setOrderInfo(order.getVnpayPayment().getOrderInfo() == null
                    ? "Thanh toan don hang OrivenJewelry " + order.getOrderCode()
                    : order.getVnpayPayment().getOrderInfo());
        }
    }

    private String resolveThumbnailUrl(Product product, ProductVariant variant) {
        if (variant.getMedia() != null && !variant.getMedia().isEmpty() && variant.getMedia().get(0) != null) {
            return variant.getMedia().get(0).getThumbnailUrl() != null
                    ? variant.getMedia().get(0).getThumbnailUrl()
                    : variant.getMedia().get(0).getUrl();
        }
        if (product.getGallery() != null && !product.getGallery().isEmpty() && product.getGallery().get(0) != null) {
            return product.getGallery().get(0).getThumbnailUrl() != null
                    ? product.getGallery().get(0).getThumbnailUrl()
                    : product.getGallery().get(0).getUrl();
        }
        return null;
    }

    private PaymentStatus resolvePaymentStatus(Integer resultCode) {
        if (resultCode == null) {
            return PaymentStatus.FAILED;
        }
        if (resultCode == 0) {
            return PaymentStatus.PAID;
        }
        if (resultCode == 1000 || resultCode == 9000) {
            return PaymentStatus.PENDING;
        }
        return PaymentStatus.FAILED;
    }

    private void maybeRestoreStockForFailedMomo(Order order, PaymentStatus nextPaymentStatus) {
        if (order.getPaymentMethod() != PaymentMethod.MOMO || nextPaymentStatus != PaymentStatus.FAILED) {
            return;
        }

        if (order.getPaymentStatus() != PaymentStatus.PENDING && order.getPaymentStatus() != PaymentStatus.UNPAID) {
            return;
        }

        restoreStock(order);
    }

    private void restoreStock(Order order) {
        Map<String, Product> touchedProducts = new LinkedHashMap<>();

        order.getItems().forEach(item -> {
            Product product = touchedProducts.computeIfAbsent(item.getProductId(), productService::findById);
            ProductVariant variant = product.getVariants().stream()
                    .filter(candidate -> item.getVariantCode().equals(candidate.getVariantCode()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Variant not found: " + item.getVariantCode()));

            variant.setStockQuantity(variant.getStockQuantity() + item.getQuantity());
            product.setTotalStock(product.getVariants().stream().mapToInt(ProductVariant::getStockQuantity).sum());
        });

        touchedProducts.values().forEach(productRepository::save);
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

    private void populateUser(Order order) {
        if (order == null || order.getUserId() == null || order.getUserId().isBlank()) {
            return;
        }
        userRepository.findById(order.getUserId()).ifPresent(u -> {
            order.setUser(new UserResponse(u.getId(), u.getEmail(), u.getFullName(), u.getRole(), u.isActive(), u.getCreatedAt(), u.getUpdatedAt()));
        });
    }

    private void populateUsers(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return;
        }
        java.util.Set<String> userIds = orders.stream()
                .map(Order::getUserId)
                .filter(Objects::nonNull)
                .filter(id -> !id.isBlank())
                .collect(java.util.stream.Collectors.toSet());
        if (userIds.isEmpty()) {
            return;
        }
        Map<String, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(java.util.stream.Collectors.toMap(User::getId, java.util.function.Function.identity()));
        orders.forEach(order -> {
            if (order.getUserId() != null) {
                User u = userMap.get(order.getUserId());
                if (u != null) {
                    order.setUser(new UserResponse(u.getId(), u.getEmail(), u.getFullName(), u.getRole(), u.isActive(), u.getCreatedAt(), u.getUpdatedAt()));
                }
            }
        });
    }

    @Transactional
    public void handleVnPayIpn(Map<String, String> params) {
        vnPayPaymentService.verifyIpn(params);
        String txnRef = params.get("vnp_TxnRef");
        Order order = findByCode(txnRef);
        if (order.getVnpayPayment() == null) {
            throw new BadRequestException("This order does not contain VNPay payment metadata");
        }
        if (!params.get("vnp_TmnCode").equals(order.getVnpayPayment().getTmnCode())) {
            throw new BadRequestException("VNPay tmnCode mismatch");
        }
        long vnpAmount = Long.parseLong(params.get("vnp_Amount"));
        if (vnpAmount != order.getVnpayPayment().getAmount() * 100) {
            throw new BadRequestException("VNPay amount mismatch");
        }
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BadRequestException("Order already confirmed");
        }

        String responseCode = params.get("vnp_ResponseCode");
        String transactionStatus = params.get("vnp_TransactionStatus");
        String transactionNo = params.get("vnp_TransactionNo");
        String bankCode = params.get("vnp_BankCode");
        String payDate = params.get("vnp_PayDate");

        PaymentStatus nextPaymentStatus = resolveVnPayPaymentStatus(responseCode, transactionStatus);
        maybeRestoreStockForFailedVnPay(order, nextPaymentStatus);

        order.getVnpayPayment().setResponseCode(responseCode);
        order.getVnpayPayment().setTransactionNo(transactionNo);
        if (bankCode != null) {
            order.getVnpayPayment().setBankCode(bankCode);
        }
        order.getVnpayPayment().setPayDate(payDate);
        order.getVnpayPayment().setResponseTime(Instant.now());
        order.setPaymentStatus(nextPaymentStatus);

        if ("00".equals(responseCode) && "00".equals(transactionStatus)) {
            order.setOrderStatus(OrderStatus.PAID);
            order.getVnpayPayment().setMessage("Success");
        } else {
            order.getVnpayPayment().setMessage("Failed: " + responseCode);
        }
        orderRepository.save(order);
    }

    private PaymentStatus resolveVnPayPaymentStatus(String responseCode, String transactionStatus) {
        if ("00".equals(responseCode) && "00".equals(transactionStatus)) {
            return PaymentStatus.PAID;
        }
        return PaymentStatus.FAILED;
    }

    private void maybeRestoreStockForFailedVnPay(Order order, PaymentStatus nextPaymentStatus) {
        if (order.getPaymentMethod() != PaymentMethod.VNPAY || nextPaymentStatus != PaymentStatus.FAILED) {
            return;
        }

        if (order.getPaymentStatus() != PaymentStatus.PENDING && order.getPaymentStatus() != PaymentStatus.UNPAID) {
            return;
        }

        restoreStock(order);
    }
}
