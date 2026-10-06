package com.railnova.controller;

import com.railnova.dto.*;
import com.railnova.entity.Train;
import com.railnova.service.TrainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trains")
@Tag(name = "Trains", description = "Train search, details, route stops, and seat availability APIs")
public class TrainController {

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    @GetMapping
    @Operation(summary = "Get all trains")
    public ResponseEntity<ApiResponse<List<Train>>> getAllTrains() {
        return ResponseEntity.ok(ApiResponse.ok("Trains fetched successfully", trainService.getAllTrains()));
    }

    @GetMapping("/search")
    @Operation(summary = "Search trains by origin, destination, date, class, with filtering and sorting")
    public ResponseEntity<ApiResponse<List<TrainSearchResultDto>>> searchTrains(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String classType,
            @RequestParam(required = false) String trainType,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String timeSlot,
            @RequestParam(required = false, defaultValue = "lowest_fare") String sortBy) {

        List<TrainSearchResultDto> results = trainService.searchTrains(
                from, to, date, classType, trainType, maxPrice, timeSlot, sortBy);
        return ResponseEntity.ok(ApiResponse.ok("Trains found: " + results.size(), results));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get basic train info by ID")
    public ResponseEntity<ApiResponse<Train>> getTrainById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Train found", trainService.getTrainById(id)));
    }

    @GetMapping("/{id}/details")
    @Operation(summary = "Get comprehensive train details including route stops, halt times, platform info, and coach classes")
    public ResponseEntity<ApiResponse<TrainDetailDto>> getTrainDetails(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.ok("Train details fetched", trainService.getTrainDetails(id, date)));
    }

    @GetMapping("/coaches/{coachId}/seats")
    @Operation(summary = "Get visual seat/berth layout and real-time booked status for a coach on a specific journey date")
    public ResponseEntity<ApiResponse<CoachSeatMapDto>> getCoachSeats(
            @PathVariable Long coachId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.ok("Coach seat map retrieved", trainService.getCoachSeats(coachId, date)));
    }
}
