package com.railnova.service.impl;

import com.railnova.entity.Station;
import com.railnova.exception.BadRequestException;
import com.railnova.exception.ResourceNotFoundException;
import com.railnova.repository.StationRepository;
import com.railnova.service.StationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StationServiceImpl implements StationService {

    private final StationRepository stationRepository;

    public StationServiceImpl(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Override
    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    @Override
    public List<Station> searchStations(String query) {
        if (query == null || query.trim().isEmpty()) {
            return stationRepository.findAll();
        }
        return stationRepository.searchStations(query.trim());
    }

    @Override
    public Station getStationById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Station not found with id: " + id));
    }

    @Override
    public Station getStationByCode(String code) {
        return stationRepository.findByCodeIgnoreCase(code.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found with code: " + code));
    }

    @Override
    @Transactional
    public Station createStation(Station station) {
        if (stationRepository.findByCodeIgnoreCase(station.getCode()).isPresent()) {
            throw new BadRequestException("Station with code " + station.getCode() + " already exists");
        }
        return stationRepository.save(station);
    }
}
