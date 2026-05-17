package com.example.cyan.order.config;

import com.example.cyan.common.model.enums.MomoRequestType;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "momo")
public class MomoProperties {

    private boolean enabled;
    private String endpoint = "https://test-payment.momo.vn/v2/gateway/api/create";
    private String partnerCode;
    private String accessKey;
    private String secretKey;
    private String redirectUrl;
    private String ipnUrl;
    private String orderType = "momo_wallet";
    private MomoRequestType requestType = MomoRequestType.CAPTURE_WALLET;
    private String lang = "vi";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
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

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
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
