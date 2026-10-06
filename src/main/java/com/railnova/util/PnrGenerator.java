package com.railnova.util;

import java.security.SecureRandom;

public class PnrGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a realistic 10-digit PNR number starting with 2-9
     */
    public static String generatePNR() {
        int firstDigit = 2 + RANDOM.nextInt(8); // 2 through 9
        StringBuilder sb = new StringBuilder();
        sb.append(firstDigit);
        for (int i = 0; i < 9; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * Generates a unique Booking Reference: e.g. RN-2026-A83K1
     */
    public static String generateBookingReference() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder("RN-2026-");
        for (int i = 0; i < 5; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * Generates a unique Order ID: e.g. order_rn_928174829
     */
    public static String generateOrderId() {
        return "order_rn_" + System.currentTimeMillis() + (100 + RANDOM.nextInt(900));
    }

    /**
     * Generates a realistic Payment ID: e.g. pay_rn_8392019382
     */
    public static String generatePaymentId() {
        return "pay_rn_" + System.currentTimeMillis() + (100 + RANDOM.nextInt(900));
    }

    /**
     * Generates a Refund ID: e.g. ref_rn_93817263
     */
    public static String generateRefundId() {
        return "ref_rn_" + System.currentTimeMillis() + (100 + RANDOM.nextInt(900));
    }
}
