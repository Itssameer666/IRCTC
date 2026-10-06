package com.railnova.repository;

import com.railnova.entity.Booking;
import com.railnova.entity.BookingPassenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingPassengerRepository extends JpaRepository<BookingPassenger, Long> {
    List<BookingPassenger> findByBooking(Booking booking);
    List<BookingPassenger> findByBookingId(Long bookingId);
}
