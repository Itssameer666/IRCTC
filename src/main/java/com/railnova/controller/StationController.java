package com.railnova.controller;

import com.railnova.dto.ApiResponse;
import com.railnova.entity.Station;
import com.railnova.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
@Tag(name = "Stations", description = "Railway Station query and management APIs")
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping
    @Operation(summary = "Get list of all stations")
    public ResponseEntity<ApiResponse<List<Station>>> getAllStations() {
        return ResponseEntity.ok(ApiResponse.ok("Stations fetched successfully", stationService.getAllStations()));
    }

    @GetMapping("/search")
    @Operation(summary = "Search stations by name, code, or city for autocomplete")
    public ResponseEntity<ApiResponse<List<Station>>> searchStations(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(ApiResponse.ok("Search results", stationService.searchStations(query)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get station details by id")
    public ResponseEntity<ApiResponse<Station>> getStationById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Station found", stationService.getStationById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Create new station (Admin only)")
    public ResponseEntity<ApiResponse<Station>> createStation(@RequestBody Station station) {
        Station created = stationService.createStation(station);
        return ResponseEntity.status(201).body(ApiResponse.created("Station created successfully", created));
    }
}
