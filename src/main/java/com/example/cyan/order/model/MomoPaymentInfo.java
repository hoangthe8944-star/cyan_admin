package com.example.cyan.order.model;

import java.time.Instant;

import com.example.cyan.common.model.enums.MomoRequestType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

public class MomoPaymentInfo {

    @Size(max = 50)
    private String partnerCode;

    @Size(max = 100)
    private String requestId;

    @Size(max = 100)
    private String momoOrderId;

    @Size(max = 255)
    private String orderInfo;

    @DecimalMin("0.0")
    private Long amount;

    private MomoRequestType requestType;

    @Size(max = 500)
    private String redirectUrl;

    @Size(max = 500)
    private String ipnUrl;

    @Size(max = 500)
    private String payUrl;

    @Size(max = 500)
    private String deeplink;

    @Size(max = 500)
    private String qrCodeUrl;

    @Size(max = 2000)
    private String extraData;

    @Size(max = 20)
    private String lang = "vi";

    @Size(max = 255)
    private String message;

    private Long transId;

    private Integer resultCode;

    private Instant responseTime;

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getMomoOrderId() {
        return momoOrderId;
    }

    public void setMomoOrderId(String momoOrderId) {
        this.momoOrderId = momoOrderId;
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

    public MomoRequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(MomoRequestType requestType) {
        this.requestType = requestType;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getIpnUrl() {
        return ipnUrl;
    }

    public void setIpnUrl(String ipnUrl) {
        this.ipnUrl = ipnUrl;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    public String getDeeplink() {
        return deeplink;
    }

    public void setDeeplink(String deeplink) {
        this.deeplink = deeplink;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTransId() {
        return transId;
    }

    public void setTransId(Long transId) {
        this.transId = transId;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public Instant getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Instant responseTime) {
        this.responseTime = responseTime;
    }
}
