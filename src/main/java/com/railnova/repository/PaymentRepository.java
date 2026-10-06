package com.railnova.repository;

import com.railnova.entity.Booking;
import com.railnova.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(String orderId);
    Optional<Payment> findByPaymentId(String paymentId);
    Optional<Payment> findByBooking(Booking booking);
    Optional<Payment> findByBookingId(Long bookingId);
}
