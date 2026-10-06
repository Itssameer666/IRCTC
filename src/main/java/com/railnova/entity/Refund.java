package com.railnova.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "refunds")
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonIgnore
    private Booking booking;

    @Column(name = "refund_id", nullable = false, unique = true, length = 100)
    private String refundId;

    @Column(name = "original_amount", nullable = false)
    private Double originalAmount;

    @Column(name = "cancellation_charges", nullable = false)
    private Double cancellationCharges;

    @Column(name = "refund_amount", nullable = false)
    private Double refundAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RefundStatus status = RefundStatus.PENDING;

    @Column(length = 255)
    private String reason;

    @Column(name = "refund_time", nullable = false)
    private LocalDateTime refundTime = LocalDateTime.now();

    public Refund() {
    }

    public Refund(Booking booking, String refundId, Double originalAmount, Double cancellationCharges,
                  Double refundAmount, RefundStatus status, String reason) {
        this.booking = booking;
        this.refundId = refundId;
        this.originalAmount = originalAmount;
        this.cancellationCharges = cancellationCharges;
        this.refundAmount = refundAmount;
        this.status = status;
        this.reason = reason;
        this.refundTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public Double getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(Double originalAmount) {
        this.originalAmount = originalAmount;
    }

    public Double getCancellationCharges() {
        return cancellationCharges;
    }

    public void setCancellationCharges(Double cancellationCharges) {
        this.cancellationCharges = cancellationCharges;
    }

    public Double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(Double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public RefundStatus getStatus() {
        return status;
    }

    public void setStatus(RefundStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(LocalDateTime refundTime) {
        this.refundTime = refundTime;
    }
}
