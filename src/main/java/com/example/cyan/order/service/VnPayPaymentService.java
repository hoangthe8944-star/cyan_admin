package com.example.cyan.order.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import com.example.cyan.common.exception.BadRequestException;
import com.example.cyan.order.config.VnPayProperties;
import com.example.cyan.order.dto.CheckoutVnPayRequest;
import com.example.cyan.order.model.Order;

@Service
@EnableConfigurationProperties(VnPayProperties.class)
public class VnPayPaymentService {

    private final VnPayProperties vnPayProperties;

    public VnPayPaymentService(VnPayProperties vnPayProperties) {
        this.vnPayProperties = vnPayProperties;
    }

    public VnPayCreatePaymentResponse createPayment(Order order, CheckoutVnPayRequest override) {
        validateConfiguration();

        String redirectUrl = firstNonBlank(override != null ? override.getRedirectUrl() : null, vnPayProperties.getReturnUrl());
        String ipnUrl = firstNonBlank(override != null ? override.getIpnUrl() : null, vnPayProperties.getIpnUrl());
        String lang = firstNonBlank(override != null ? override.getLang() : null, "vi");
        String bankCode = override != null ? override.getBankCode() : null;

        if (isBlank(redirectUrl)) {
            throw new BadRequestException("VNPay returnUrl/redirectUrl is required");
        }

        order.getVnpayPayment().setTmnCode(vnPayProperties.getTmnCode());
        order.getVnpayPayment().setTxnRef(order.getOrderCode());

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", vnPayProperties.getVersion());
        vnpParams.put("vnp_Command", vnPayProperties.getCommand());
        vnpParams.put("vnp_TmnCode", vnPayProperties.getTmnCode());
        vnpParams.put("vnp_Amount", String.valueOf(order.getVnpayPayment().getAmount() * 100));
        vnpParams.put("vnp_CurrCode", "VND");

        if (bankCode != null && !bankCode.isBlank()) {
            vnpParams.put("vnp_BankCode", bankCode);
            order.getVnpayPayment().setBankCode(bankCode);
        }

        vnpParams.put("vnp_TxnRef", order.getOrderCode());
        vnpParams.put("vnp_OrderInfo", order.getVnpayPayment().getOrderInfo());
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", lang);
        vnpParams.put("vnp_ReturnUrl", redirectUrl);
        vnpParams.put("vnp_IpAddr", "127.0.0.1");

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT-7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_CreateDate", vnpCreateDate);

        // Sort keys
        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnpParams.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(urlEncode(fieldValue));
                // Build query
                query.append(urlEncode(fieldName));
                query.append('=');
                query.append(urlEncode(fieldValue));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnpSecureHash = hmacSHA512(vnPayProperties.getSecretKey(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;

        String paymentUrl = vnPayProperties.getUrl() + "?" + queryUrl;

        order.getVnpayPayment().setPayUrl(paymentUrl);
        order.getVnpayPayment().setSecureHash(vnpSecureHash);

        return new VnPayCreatePaymentResponse(paymentUrl, vnpSecureHash);
    }

    public void verifyIpn(Map<String, String> fields) {
        validateConfiguration();
        String vnpSecureHash = fields.get("vnp_SecureHash");
        if (isBlank(vnpSecureHash)) {
            throw new BadRequestException("vnp_SecureHash is missing");
        }

        // Filter and sort keys
        Map<String, String> signedFields = new HashMap<>();
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null && key.startsWith("vnp_") && !key.equals("vnp_SecureHash") && !key.equals("vnp_SecureHashType")) {
                signedFields.put(key, value);
            }
        }

        List<String> fieldNames = new ArrayList<>(signedFields.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = signedFields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(urlEncode(fieldValue));
                if (itr.hasNext()) {
                    hashData.append('&');
                }
            }
        }

        String computedHash = hmacSHA512(vnPayProperties.getSecretKey(), hashData.toString());
        if (!computedHash.equalsIgnoreCase(vnpSecureHash)) {
            throw new BadRequestException("Invalid VNPay signature");
        }
    }

    private void validateConfiguration() {
        if (!vnPayProperties.isEnabled()) {
            throw new BadRequestException("VNPay payment is not enabled");
        }
        if (isBlank(vnPayProperties.getTmnCode()) || isBlank(vnPayProperties.getSecretKey())) {
            throw new BadRequestException("VNPay configuration is incomplete");
        }
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString()).replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    private String firstNonBlank(String primary, String fallback) {
        return isBlank(primary) ? fallback : primary;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String hmacSHA512(String key, String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac.init(secretKey);
            byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(bytes.length * 2);
            for (byte value : bytes) {
                builder.append(String.format(Locale.ROOT, "%02x", value & 0xff));
            }
            return builder.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to sign VNPay request", ex);
        }
    }

    public record VnPayCreatePaymentResponse(
            String payUrl,
            String secureHash) {
    }
}
