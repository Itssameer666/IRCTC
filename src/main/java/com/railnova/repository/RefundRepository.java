package com.railnova.repository;

import com.railnova.entity.Booking;
import com.railnova.entity.Refund;
import com.railnova.entity.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
    Optional<Refund> findByRefundId(String refundId);
    List<Refund> findByBooking(Booking booking);
    List<Refund> findByBookingId(Long bookingId);
    List<Refund> findByStatus(RefundStatus status);
    long countByStatus(RefundStatus status);
    List<Refund> findAllByOrderByRefundTimeDesc();
}
