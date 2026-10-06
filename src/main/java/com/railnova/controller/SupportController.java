package com.railnova.controller;

import com.railnova.dto.ApiResponse;
import com.railnova.dto.BookingResponseDto;
import com.railnova.dto.SupportTicketRequestDto;
import com.railnova.dto.SupportTicketResponseDto;
import com.railnova.service.BookingService;
import com.railnova.service.SupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Customer Support & Staff", description = "Ticket submission, staff resolution, and passenger assistance")
public class SupportController {

    private final SupportService supportService;
    private final BookingService bookingService;

    public SupportController(SupportService supportService, BookingService bookingService) {
        this.supportService = supportService;
        this.bookingService = bookingService;
    }

    @PostMapping("/support/tickets")
    @Operation(summary = "Submit support ticket for query or booking grievance")
    public ResponseEntity<ApiResponse<SupportTicketResponseDto>> createTicket(
            @Valid @RequestBody SupportTicketRequestDto dto,
            Authentication auth) {
        SupportTicketResponseDto ticket = supportService.createTicket(dto, auth.getName());
        return ResponseEntity.status(201).body(ApiResponse.created("Ticket submitted successfully", ticket));
    }

    @GetMapping("/support/tickets/my")
    @Operation(summary = "Get current user support tickets")
    public ResponseEntity<ApiResponse<List<SupportTicketResponseDto>>> getMyTickets(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok("User tickets retrieved", supportService.getUserTickets(auth.getName())));
    }

    @GetMapping("/staff/tickets")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')")
    @Operation(summary = "Get all customer support tickets (Staff/Admin)")
    public ResponseEntity<ApiResponse<List<SupportTicketResponseDto>>> getAllTickets() {
        return ResponseEntity.ok(ApiResponse.ok("All tickets retrieved", supportService.getAllTickets()));
    }

    @PutMapping("/staff/tickets/{id}/reply")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')")
    @Operation(summary = "Reply to customer ticket and update status")
    public ResponseEntity<ApiResponse<SupportTicketResponseDto>> replyTicket(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication auth) {
        String reply = body.get("reply");
        SupportTicketResponseDto response = supportService.replyTicket(id, reply, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Reply submitted successfully", response));
    }

    @GetMapping("/staff/bookings")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')")
    @Operation(summary = "Staff view for passenger bookings and verification")
    public ResponseEntity<ApiResponse<List<BookingResponseDto>>> getStaffBookings() {
        return ResponseEntity.ok(ApiResponse.ok("Bookings retrieved for staff", bookingService.getAllBookings()));
    }
}
