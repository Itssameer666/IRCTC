package com.railnova.service;

import com.railnova.dto.*;
import com.railnova.entity.Train;

import java.time.LocalDate;
import java.util.List;

public interface TrainService {
    List<TrainSearchResultDto> searchTrains(String fromStation, String toStation, LocalDate journeyDate,
                                           String classType, String trainType, Double maxPrice,
                                           String timeSlot, String sortBy);

    TrainDetailDto getTrainDetails(Long trainId, LocalDate journeyDate);

    CoachSeatMapDto getCoachSeats(Long coachId, LocalDate journeyDate);

    List<Train> getAllTrains();

    Train getTrainById(Long id);

    Train createTrain(TrainCreateUpdateDto dto);

    Train updateTrain(Long id, TrainCreateUpdateDto dto);

    void deleteTrain(Long id);

    Train toggleTrainStatus(Long id);
}
