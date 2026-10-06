package com.railnova.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class SignatureUtil {

    private static final String HMAC_SHA256 = "HmacSHA256";

    /**
     * Computes HMAC-SHA256 hex string for (orderId + "|" + paymentId) using keySecret
     */
    public static String calculateSignature(String orderId, String paymentId, String secret) {
        try {
            String data = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error calculating payment signature: " + e.getMessage(), e);
        }
    }

    /**
     * Constant-time comparison to protect against timing attacks
     */
    public static boolean verifySignature(String orderId, String paymentId, String receivedSignature, String secret) {
        if (orderId == null || paymentId == null || receivedSignature == null || secret == null) {
            return false;
        }
        String calculated = calculateSignature(orderId, paymentId, secret);
        return MessageDigest.isEqual(
                calculated.getBytes(StandardCharsets.UTF_8),
                receivedSignature.getBytes(StandardCharsets.UTF_8)
        );
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
