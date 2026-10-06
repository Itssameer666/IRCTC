package com.railnova.service;

import com.railnova.dto.BookingRequestDto;
import com.railnova.dto.BookingResponseDto;
import com.railnova.dto.CancelBookingRequestDto;
import com.railnova.entity.Booking;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto request, String userEmail);
    BookingResponseDto getBookingById(Long id);
    BookingResponseDto getBookingByPnr(String pnr);
    List<BookingResponseDto> getUserBookings(String userEmail);
    BookingResponseDto cancelBooking(Long bookingId, CancelBookingRequestDto request, String userEmail);
    List<BookingResponseDto> getAllBookings();
    Booking getBookingEntityById(Long id);
    void confirmBooking(Long bookingId);
}
