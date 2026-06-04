package com.example.cyan.order.dto;

import jakarta.validation.constraints.Size;

public class CheckoutVnPayRequest {

    @Size(max = 255)
    private String orderInfo;

    @Size(max = 500)
    private String redirectUrl;

    @Size(max = 500)
    private String ipnUrl;

    @Size(max = 50)
    private String bankCode;

    @Size(max = 20)
    private String lang;

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
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

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
