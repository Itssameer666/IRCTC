package com.railnova.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentVerifyDto {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotBlank(message = "Payment ID is required")
    private String paymentId;

    @NotBlank(message = "Payment signature is required")
    private String signature;

    private String paymentMethod = "UPI"; // CARD, UPI, NET_BANKING, WALLET

    public PaymentVerifyDto() {
    }

    public PaymentVerifyDto(Long bookingId, String orderId, String paymentId, String signature, String paymentMethod) {
        this.bookingId = bookingId;
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.signature = signature;
        this.paymentMethod = paymentMethod;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
