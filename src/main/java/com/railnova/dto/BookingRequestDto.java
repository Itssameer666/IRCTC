package com.railnova.dto;

import com.railnova.entity.CoachType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class BookingRequestDto {

    @NotNull(message = "Train ID is required")
    private Long trainId;

    @NotNull(message = "Journey date is required")
    private LocalDate journeyDate;

    @NotNull(message = "Coach class is required")
    private CoachType coachType;

    private Long sourceStationId;

    private Long destinationStationId;

    @NotEmpty(message = "At least one passenger is required")
    @Valid
    private List<PassengerRequestDto> passengers;

    public BookingRequestDto() {
    }

    public BookingRequestDto(Long trainId, LocalDate journeyDate, CoachType coachType,
                             Long sourceStationId, Long destinationStationId,
                             List<PassengerRequestDto> passengers) {
        this.trainId = trainId;
        this.journeyDate = journeyDate;
        this.coachType = coachType;
        this.sourceStationId = sourceStationId;
        this.destinationStationId = destinationStationId;
        this.passengers = passengers;
    }

    public Long getTrainId() {
        return trainId;
    }

    public void setTrainId(Long trainId) {
        this.trainId = trainId;
    }

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
    }

    public CoachType getCoachType() {
        return coachType;
    }

    public void setCoachType(CoachType coachType) {
        this.coachType = coachType;
    }

    public Long getSourceStationId() {
        return sourceStationId;
    }

    public void setSourceStationId(Long sourceStationId) {
        this.sourceStationId = sourceStationId;
    }

    public Long getDestinationStationId() {
        return destinationStationId;
    }

    public void setDestinationStationId(Long destinationStationId) {
        this.destinationStationId = destinationStationId;
    }

    public List<PassengerRequestDto> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PassengerRequestDto> passengers) {
        this.passengers = passengers;
    }
}
