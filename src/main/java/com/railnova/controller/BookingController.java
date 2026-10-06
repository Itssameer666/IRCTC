package com.railnova.controller;

import com.railnova.dto.*;
import com.railnova.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Bookings", description = "Ticket booking, seat locking, cancellation and history APIs")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @Operation(summary = "Create ticket booking with passenger details and berth selection")
    public ResponseEntity<ApiResponse<BookingResponseDto>> createBooking(
            @Valid @RequestBody BookingRequestDto request,
            Authentication authentication) {
        BookingResponseDto booking = bookingService.createBooking(request, authentication.getName());
        return ResponseEntity.status(201).body(ApiResponse.created("Booking created successfully. Please complete payment.", booking));
    }

    @GetMapping("/my")
    @Operation(summary = "Get booking history for the current logged-in passenger")
    public ResponseEntity<ApiResponse<List<BookingResponseDto>>> getMyBookings(Authentication authentication) {
        List<BookingResponseDto> bookings = bookingService.getUserBookings(authentication.getName());
        return ResponseEntity.ok(ApiResponse.ok("User bookings fetched", bookings));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking details by ID")
    public ResponseEntity<ApiResponse<BookingResponseDto>> getBookingById(@PathVariable Long id) {
        BookingResponseDto booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(ApiResponse.ok("Booking details", booking));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel an existing confirmed booking and initiate refund")
    public ResponseEntity<ApiResponse<BookingResponseDto>> cancelBooking(
            @PathVariable Long id,
            @RequestBody(required = false) CancelBookingRequestDto request,
            Authentication authentication) {
        BookingResponseDto cancelled = bookingService.cancelBooking(id, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.ok("Booking cancelled successfully and refund initiated", cancelled));
    }
}
