package com.railnova.repository;

import com.railnova.entity.Booking;
import com.railnova.entity.BookingStatus;
import com.railnova.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByPnrNumber(String pnrNumber);
    Optional<Booking> findByBookingReference(String bookingReference);
    List<Booking> findByUserOrderByBookingTimeDesc(User user);
    List<Booking> findByUserIdOrderByBookingTimeDesc(Long userId);
    List<Booking> findAllByOrderByBookingTimeDesc();

    long countByBookingStatus(BookingStatus status);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.bookingTime >= :startOfDay")
    long countTodayBookings(LocalDateTime startOfDay);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0.0) FROM Booking b WHERE b.bookingStatus = 'CONFIRMED'")
    Double calculateTotalRevenue();

    List<Booking> findByTrainIdAndJourneyDate(Long trainId, LocalDate journeyDate);
}
