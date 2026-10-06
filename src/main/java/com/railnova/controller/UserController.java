package com.railnova.controller;

import com.railnova.dto.ApiResponse;
import com.railnova.dto.UserDto;
import com.railnova.entity.Notification;
import com.railnova.entity.SavedRoute;
import com.railnova.service.NotificationService;
import com.railnova.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Profile updates, saved routes, and notifications")
public class UserController {

    private final UserService userService;
    private final NotificationService notificationService;

    public UserController(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping("/profile")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<ApiResponse<UserDto>> getProfile(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok("Profile retrieved", userService.getUserProfile(auth.getName())));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user name or phone number")
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(
            @RequestBody Map<String, String> body,
            Authentication auth) {
        String fullName = body.get("fullName");
        String phone = body.get("phone");
        UserDto updated = userService.updateUserProfile(auth.getName(), fullName, phone);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated", updated));
    }

    @GetMapping("/saved-routes")
    @Operation(summary = "Get user's favourite saved routes")
    public ResponseEntity<ApiResponse<List<SavedRoute>>> getSavedRoutes(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok("Saved routes retrieved", userService.getSavedRoutes(auth.getName())));
    }

    @PostMapping("/saved-routes")
    @Operation(summary = "Save a route to favourites")
    public ResponseEntity<ApiResponse<SavedRoute>> saveRoute(
            @RequestParam Long sourceStationId,
            @RequestParam Long destinationStationId,
            Authentication auth) {
        SavedRoute sr = userService.saveRoute(auth.getName(), sourceStationId, destinationStationId);
        return ResponseEntity.status(201).body(ApiResponse.created("Route saved", sr));
    }

    @DeleteMapping("/saved-routes/{id}")
    @Operation(summary = "Delete saved route")
    public ResponseEntity<ApiResponse<Void>> deleteSavedRoute(@PathVariable Long id, Authentication auth) {
        userService.deleteSavedRoute(auth.getName(), id);
        return ResponseEntity.ok(ApiResponse.ok("Saved route removed", null));
    }

    @GetMapping("/notifications")
    @Operation(summary = "Get user's travel notifications and alerts")
    public ResponseEntity<ApiResponse<List<Notification>>> getNotifications(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok("Notifications retrieved", notificationService.getUserNotifications(auth.getName())));
    }

    @PutMapping("/notifications/{id}/read")
    @Operation(summary = "Mark notification as read")
    public ResponseEntity<ApiResponse<Void>> markNotificationRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.ok("Notification marked as read", null));
    }
}
