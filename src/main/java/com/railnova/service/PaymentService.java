package com.railnova.service;

import com.railnova.dto.BookingResponseDto;
import com.railnova.dto.PaymentOrderDto;
import com.railnova.dto.PaymentVerifyDto;
import com.railnova.entity.Payment;

public interface PaymentService {
    PaymentOrderDto createOrder(Long bookingId, String userEmail);
    BookingResponseDto verifyPayment(PaymentVerifyDto verifyDto, String userEmail);
    Payment getPaymentByBooking(Long bookingId);
}
