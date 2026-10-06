package com.railnova.controller;

import com.railnova.dto.*;
import com.railnova.service.PaymentService;
import com.railnova.service.impl.PaymentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments", description = "Official Payment Gateway Order creation and HMAC Signature Verification")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order")
    @Operation(summary = "Create payment order for booking (Sandbox)")
    public ResponseEntity<ApiResponse<PaymentOrderDto>> createOrder(
            @RequestParam Long bookingId,
            Authentication authentication) {
        PaymentOrderDto order = paymentService.createOrder(bookingId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.ok("Payment order created", order));
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify HMAC-SHA256 signature and confirm booking")
    public ResponseEntity<ApiResponse<BookingResponseDto>> verifyPayment(
            @Valid @RequestBody PaymentVerifyDto verifyDto,
            Authentication authentication) {
        BookingResponseDto booking = paymentService.verifyPayment(verifyDto, authentication.getName());
        return ResponseEntity.ok(ApiResponse.ok("Payment verified and booking confirmed", booking));
    }

    @GetMapping("/sandbox-sign")
    @Operation(summary = "Sandbox test helper: returns HMAC SHA256 signature for test gateway execution")
    public ResponseEntity<ApiResponse<Map<String, String>>> getSandboxSignature(
            @RequestParam String orderId,
            @RequestParam String paymentId) {
        String sig = ((PaymentServiceImpl) paymentService).getSandboxSignature(orderId, paymentId);
        return ResponseEntity.ok(ApiResponse.ok("Signature calculated", Map.of("signature", sig)));
    }
}
