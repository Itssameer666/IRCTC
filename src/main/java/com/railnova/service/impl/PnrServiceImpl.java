package com.railnova.service.impl;

import com.railnova.dto.PnrResponseDto;
import com.railnova.entity.Booking;
import com.railnova.exception.ResourceNotFoundException;
import com.railnova.repository.BookingRepository;
import com.railnova.service.PnrService;
import org.springframework.stereotype.Service;

@Service
public class PnrServiceImpl implements PnrService {

    private final BookingRepository bookingRepository;

    public PnrServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public PnrResponseDto getPnrStatus(String pnr) {
        if (pnr == null || pnr.trim().isEmpty()) {
            throw new ResourceNotFoundException("Please provide a valid 10-digit PNR number.");
        }
        Booking booking = bookingRepository.findByPnrNumber(pnr.trim())
                .orElseThrow(() -> new ResourceNotFoundException("No booking found for PNR: " + pnr.trim() + ". Please verify and try again."));
        return new PnrResponseDto(booking);
    }
}
