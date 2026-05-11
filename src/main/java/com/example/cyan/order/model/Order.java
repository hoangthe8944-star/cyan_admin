package com.example.cyan.order.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.cyan.common.model.BaseDocument;
import com.example.cyan.common.model.enums.OrderStatus;
import com.example.cyan.common.model.enums.PaymentMethod;
import com.example.cyan.common.model.enums.PaymentStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Document("orders")
public class Order extends BaseDocument {

    @Indexed(unique = true)
    @NotBlank
    @Size(max = 60)
    private String orderCode;

    @Valid
    @NotNull
    private CustomerInfo customer;

    @Valid
    @NotNull
    private Address shippingAddress;

    @Valid
    private Address billingAddress;

    @Valid
    @NotEmpty
    private List<OrderItem> items = new ArrayList<>();

    @DecimalMin("0.0")
    private BigDecimal subtotal = BigDecimal.ZERO;

    @DecimalMin("0.0")
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @DecimalMin("0.0")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @DecimalMin("0.0")
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Size(max = 20)
    private String currency = "VND";

    @NotNull
    private PaymentMethod paymentMethod = PaymentMethod.MOMO;

    @NotNull
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @NotNull
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @Size(max = 500)
    private String note;

    @Valid
    private MomoPaymentInfo momoPayment;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public CustomerInfo getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerInfo customer) {
        this.customer = customer;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public MomoPaymentInfo getMomoPayment() {
        return momoPayment;
    }

    public void setMomoPayment(MomoPaymentInfo momoPayment) {
        this.momoPayment = momoPayment;
    }
}
