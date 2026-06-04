package com.example.cyan.order.model;

import java.time.Instant;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

public class VnPayPaymentInfo {

    @Size(max = 50)
    private String tmnCode;

    @Size(max = 100)
    private String txnRef;

    @Size(max = 255)
    private String orderInfo;

    @DecimalMin("0.0")
    private Long amount;

    @Size(max = 2000)
    private String payUrl;

    @Size(max = 255)
    private String secureHash;

    @Size(max = 50)
    private String responseCode;

    @Size(max = 50)
    private String transactionNo;

    @Size(max = 50)
    private String bankCode;

    @Size(max = 50)
    private String payDate;

    @Size(max = 255)
    private String message;

    private Instant responseTime;

    public String getTmnCode() {
        return tmnCode;
    }

    public void setTmnCode(String tmnCode) {
        this.tmnCode = tmnCode;
    }

    public String getTxnRef() {
        return txnRef;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    public String getSecureHash() {
        return secureHash;
    }

    public void setSecureHash(String secureHash) {
        this.secureHash = secureHash;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Instant responseTime) {
        this.responseTime = responseTime;
    }
}
