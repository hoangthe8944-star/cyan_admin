package com.example.cyan.order.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.cyan.common.model.enums.PaymentMethod;
import com.example.cyan.order.model.Address;
import com.example.cyan.order.model.CustomerInfo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateOrderRequest {

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
    private List<CheckoutOrderItemRequest> items = new ArrayList<>();

    @DecimalMin("0.0")
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @DecimalMin("0.0")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @NotNull
    private PaymentMethod paymentMethod = PaymentMethod.MOMO;

    @Size(max = 500)
    private String note;

    @Valid
    private CheckoutMomoRequest momoPayment;

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

    public List<CheckoutOrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<CheckoutOrderItemRequest> items) {
        this.items = items;
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

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public CheckoutMomoRequest getMomoPayment() {
        return momoPayment;
    }

    public void setMomoPayment(CheckoutMomoRequest momoPayment) {
        this.momoPayment = momoPayment;
    }
}
