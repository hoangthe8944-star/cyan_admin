package com.example.cyan.order.dto;

import com.example.cyan.common.model.enums.MomoRequestType;

import jakarta.validation.constraints.Size;

public class CheckoutMomoRequest {

    @Size(max = 255)
    private String orderInfo;

    @Size(max = 500)
    private String redirectUrl;

    @Size(max = 500)
    private String ipnUrl;

    @Size(max = 2000)
    private String extraData;

    private MomoRequestType requestType;

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

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    public MomoRequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(MomoRequestType requestType) {
        this.requestType = requestType;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
