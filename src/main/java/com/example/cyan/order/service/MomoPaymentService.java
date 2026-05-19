package com.example.cyan.order.service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.example.cyan.common.exception.BadRequestException;
import com.example.cyan.common.model.enums.MomoRequestType;
import com.example.cyan.order.config.MomoProperties;
import com.example.cyan.order.dto.CheckoutMomoRequest;
import com.example.cyan.order.dto.MomoIpnRequest;
import com.example.cyan.order.model.Order;

@Service
@EnableConfigurationProperties(MomoProperties.class)
public class MomoPaymentService {

    private final MomoProperties momoProperties;
    private final HttpClient httpClient;

    public MomoPaymentService(MomoProperties momoProperties) {
        this.momoProperties = momoProperties;
        this.httpClient = HttpClient.newHttpClient();
    }

    public MomoCreatePaymentResponse createPayment(Order order, CheckoutMomoRequest override) {
        validateConfiguration();

        String requestType = requestTypeValue(override != null ? override.getRequestType() : momoProperties.getRequestType());
        String redirectUrl = firstNonBlank(override != null ? override.getRedirectUrl() : null, momoProperties.getRedirectUrl());
        String ipnUrl = firstNonBlank(override != null ? override.getIpnUrl() : null, momoProperties.getIpnUrl());
        String extraData = override != null && override.getExtraData() != null ? override.getExtraData() : "";
        String orderId = order.getMomoPayment().getMomoOrderId();
        String requestId = order.getMomoPayment().getRequestId();
        String orderInfo = order.getMomoPayment().getOrderInfo();
        long amount = order.getMomoPayment().getAmount();
        String lang = firstNonBlank(override != null ? override.getLang() : null, momoProperties.getLang());

        if (isBlank(redirectUrl) || isBlank(ipnUrl)) {
            throw new BadRequestException("MoMo redirectUrl and ipnUrl are required");
        }

        order.getMomoPayment().setPartnerCode(momoProperties.getPartnerCode());
        order.getMomoPayment().setRedirectUrl(redirectUrl);
        order.getMomoPayment().setIpnUrl(ipnUrl);
        order.getMomoPayment().setExtraData(extraData);
        order.getMomoPayment().setLang(lang);

        String rawSignature = "accessKey=" + momoProperties.getAccessKey()
                + "&amount=" + amount
                + "&extraData=" + extraData
                + "&ipnUrl=" + ipnUrl
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&partnerCode=" + momoProperties.getPartnerCode()
                + "&redirectUrl=" + redirectUrl
                + "&requestId=" + requestId
                + "&requestType=" + requestType;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(momoProperties.getEndpoint()))
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .POST(HttpRequest.BodyPublishers.ofString(buildCreatePayload(order, requestType, redirectUrl, ipnUrl,
                            extraData, lang, sign(rawSignature))))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new BadRequestException(
                        "MoMo create payment failed: HTTP " + response.statusCode() + " - "
                                + messageOrDefault(extractString(body, "message"), "Unknown MoMo error"));
            }
            int resultCode = intValue(extractNumber(body, "resultCode"));
            if (resultCode != 0 && resultCode != 1000) {
                throw new BadRequestException(
                        "MoMo create payment failed: " + messageOrDefault(extractString(body, "message"),
                                "Unknown MoMo error"));
            }

            return new MomoCreatePaymentResponse(
                    extractString(body, "payUrl"),
                    extractString(body, "deeplink"),
                    extractString(body, "qrCodeUrl"),
                    extractString(body, "message"),
                    resultCode,
                    longValue(extractNumber(body, "responseTime")));
        } catch (BadRequestException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new BadRequestException("Unable to connect to MoMo payment gateway");
        } catch (Exception ex) {
            throw new BadRequestException(
                    "Unable to connect to MoMo payment gateway: " + messageOrDefault(ex.getMessage(), ex.getClass().getSimpleName()));
        }
    }

    public void verifyIpn(MomoIpnRequest request) {
        validateConfiguration();
        String rawSignature = "accessKey=" + momoProperties.getAccessKey()
                + "&amount=" + request.getAmount()
                + "&extraData=" + defaultString(request.getExtraData())
                + "&message=" + request.getMessage()
                + "&orderId=" + request.getOrderId()
                + "&orderInfo=" + request.getOrderInfo()
                + "&orderType=" + request.getOrderType()
                + "&partnerCode=" + request.getPartnerCode()
                + "&payType=" + request.getPayType()
                + "&requestId=" + request.getRequestId()
                + "&responseTime=" + request.getResponseTime()
                + "&resultCode=" + request.getResultCode()
                + "&transId=" + request.getTransId();

        String computedSignature = sign(rawSignature);
        if (!Objects.equals(computedSignature, request.getSignature())) {
            throw new BadRequestException("Invalid MoMo signature");
        }
    }

    private void validateConfiguration() {
        if (!momoProperties.isEnabled()) {
            throw new BadRequestException("MoMo payment is not enabled");
        }
        if (isBlank(momoProperties.getPartnerCode()) || isBlank(momoProperties.getAccessKey())
                || isBlank(momoProperties.getSecretKey())) {
            throw new BadRequestException("MoMo configuration is incomplete");
        }
    }

    private String buildCreatePayload(Order order, String requestType, String redirectUrl, String ipnUrl,
            String extraData, String lang, String signature) {
        return "{"
                + "\"partnerCode\":" + json(momoProperties.getPartnerCode()) + ","
                + "\"requestId\":" + json(order.getMomoPayment().getRequestId()) + ","
                + "\"amount\":" + order.getMomoPayment().getAmount() + ","
                + "\"orderId\":" + json(order.getMomoPayment().getMomoOrderId()) + ","
                + "\"orderInfo\":" + json(order.getMomoPayment().getOrderInfo()) + ","
                + "\"redirectUrl\":" + json(redirectUrl) + ","
                + "\"ipnUrl\":" + json(ipnUrl) + ","
                + "\"requestType\":" + json(requestType) + ","
                + "\"extraData\":" + json(extraData) + ","
                + "\"lang\":" + json(lang) + ","
                + "\"orderType\":" + json(momoProperties.getOrderType()) + ","
                + "\"items\":" + buildItemsJson(order) + ","
                + "\"signature\":" + json(signature)
                + "}";
    }

    private String buildItemsJson(Order order) {
        return order.getItems().stream()
                .limit(50)
                .map(item -> "{"
                        + "\"id\":" + json(item.getVariantCode()) + ","
                        + "\"name\":" + json(item.getProductName()) + ","
                        + "\"description\":" + json(item.getProductName()) + ","
                        + "\"category\":" + json("product") + ","
                        + "\"imageUrl\":" + json(item.getThumbnailUrl()) + ","
                        + "\"price\":" + money(item.getUnitPrice()) + ","
                        + "\"currency\":" + json(order.getCurrency()) + ","
                        + "\"quantity\":" + item.getQuantity() + ","
                        + "\"unit\":" + json("item") + ","
                        + "\"totalPrice\":" + money(item.getLineTotal())
                        + "}")
                .reduce("[", (left, right) -> left.equals("[") ? left + right : left + "," + right)
                + "]";
    }

    private String json(String value) {
        if (value == null) {
            return "null";
        }
        return "\"" + value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n") + "\"";
    }

    private String sign(String rawSignature) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(momoProperties.getSecretKey().getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256");
            hmac.init(secretKey);
            byte[] bytes = hmac.doFinal(rawSignature.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(bytes.length * 2);
            for (byte value : bytes) {
                builder.append(String.format(Locale.ROOT, "%02x", value));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new IllegalStateException("Unable to sign MoMo request", ex);
        }
    }

    private String requestTypeValue(MomoRequestType requestType) {
        MomoRequestType value = requestType == null ? MomoRequestType.CAPTURE_WALLET : requestType;
        return switch (value) {
            case CAPTURE_WALLET -> "captureWallet";
            case PAY_WITH_ATM -> "payWithATM";
            case PAY_WITH_CC -> "payWithCC";
            case PAY_WITH_QR_CODE -> "captureWallet";
        };
    }

    private long money(BigDecimal value) {
        return value == null ? 0L : value.longValue();
    }

    private String firstNonBlank(String primary, String fallback) {
        return isBlank(primary) ? fallback : primary;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private String messageOrDefault(String value, String fallback) {
        return isBlank(value) ? fallback : value;
    }

    private String extractString(String json, String key) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"")
                .matcher(json);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1)
                .replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\r", "\r")
                .replace("\\n", "\n");
    }

    private String extractNumber(String json, String key) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(-?\\d+)")
                .matcher(json);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1);
    }

    private int intValue(String value) {
        return value == null ? -1 : Integer.parseInt(value);
    }

    private long longValue(String value) {
        return value == null ? 0L : Long.parseLong(value);
    }

    public record MomoCreatePaymentResponse(
            String payUrl,
            String deeplink,
            String qrCodeUrl,
            String message,
            int resultCode,
            Long responseTime) {
    }
}
