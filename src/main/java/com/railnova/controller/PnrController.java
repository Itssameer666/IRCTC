package com.railnova.controller;

import com.railnova.dto.ApiResponse;
import com.railnova.dto.PnrResponseDto;
import com.railnova.service.PnrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pnr")
@Tag(name = "PNR Enquiry", description = "Live 10-digit PNR status and passenger coach/seat enquiry")
public class PnrController {

    private final PnrService pnrService;

    public PnrController(PnrService pnrService) {
        this.pnrService = pnrService;
    }

    @GetMapping("/{pnr}")
    @Operation(summary = "Get live PNR status, coach, berth, journey details")
    public ResponseEntity<ApiResponse<PnrResponseDto>> getPnrStatus(@PathVariable String pnr) {
        PnrResponseDto response = pnrService.getPnrStatus(pnr);
        return ResponseEntity.ok(ApiResponse.ok("PNR details retrieved successfully", response));
    }
}
