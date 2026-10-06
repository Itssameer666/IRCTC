package com.railnova.service;

import com.railnova.entity.Station;
import java.util.List;

public interface StationService {
    List<Station> getAllStations();
    List<Station> searchStations(String query);
    Station getStationById(Long id);
    Station getStationByCode(String code);
    Station createStation(Station station);
}
