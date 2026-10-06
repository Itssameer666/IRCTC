package com.railnova.controller;

import com.railnova.dto.*;
import com.railnova.entity.AuditLog;
import com.railnova.entity.Refund;
import com.railnova.entity.Train;
import com.railnova.service.AdminService;
import com.railnova.service.BookingService;
import com.railnova.service.TrainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Admin Operations", description = "Admin dashboard statistics, user management, and train operations")
public class AdminController {

    private final AdminService adminService;
    private final TrainService trainService;
    private final BookingService bookingService;

    public AdminController(AdminService adminService,
                           TrainService trainService,
                           BookingService bookingService) {
        this.adminService = adminService;
        this.trainService = trainService;
        this.bookingService = bookingService;
    }

    @GetMapping("/stats")
    @Operation(summary = "Get aggregated metrics and chart analytics for admin dashboard")
    public ResponseEntity<ApiResponse<AdminStatsDto>> getStats() {
        return ResponseEntity.ok(ApiResponse.ok("Admin stats retrieved", adminService.getAdminStats()));
    }

    @GetMapping("/users")
    @Operation(summary = "Get all registered users")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.ok("Users retrieved", adminService.getAllUsers()));
    }

    @PutMapping("/users/{id}/toggle-status")
    @Operation(summary = "Activate or deactivate a user account")
    public ResponseEntity<ApiResponse<UserDto>> toggleUserStatus(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("User status updated", adminService.toggleUserStatus(id)));
    }

    @PostMapping("/trains")
    @Operation(summary = "Create a new train schedule with coaches and fares")
    public ResponseEntity<ApiResponse<Train>> createTrain(@Valid @RequestBody TrainCreateUpdateDto dto) {
        Train train = trainService.createTrain(dto);
        return ResponseEntity.status(201).body(ApiResponse.created("Train created successfully", train));
    }

    @PutMapping("/trains/{id}")
    @Operation(summary = "Update train details")
    public ResponseEntity<ApiResponse<Train>> updateTrain(
            @PathVariable Long id,
            @Valid @RequestBody TrainCreateUpdateDto dto) {
        Train train = trainService.updateTrain(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Train updated successfully", train));
    }

    @DeleteMapping("/trains/{id}")
    @Operation(summary = "Delete train by ID")
    public ResponseEntity<ApiResponse<Void>> deleteTrain(@PathVariable Long id) {
        trainService.deleteTrain(id);
        return ResponseEntity.ok(ApiResponse.ok("Train deleted successfully", null));
    }

    @PutMapping("/trains/{id}/toggle-status")
    @Operation(summary = "Toggle train active status")
    public ResponseEntity<ApiResponse<Train>> toggleTrainStatus(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Train status toggled", trainService.toggleTrainStatus(id)));
    }

    @GetMapping("/bookings")
    @Operation(summary = "Get all platform bookings")
    public ResponseEntity<ApiResponse<List<BookingResponseDto>>> getAllBookings() {
        return ResponseEntity.ok(ApiResponse.ok("All bookings retrieved", bookingService.getAllBookings()));
    }

    @GetMapping("/refunds")
    @Operation(summary = "Get all processed and pending refunds")
    public ResponseEntity<ApiResponse<List<Refund>>> getAllRefunds() {
        return ResponseEntity.ok(ApiResponse.ok("Refund records retrieved", adminService.getAllRefunds()));
    }

    @GetMapping("/audit-logs")
    @Operation(summary = "Get system audit logs")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAuditLogs() {
        return ResponseEntity.ok(ApiResponse.ok("Audit logs retrieved", adminService.getAuditLogs()));
    }
}
