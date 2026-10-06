package com.railnova.dto;

import com.railnova.entity.TrainType;
import java.util.ArrayList;
import java.util.List;

public class TrainSearchResultDto {
    private Long id;
    private String trainNumber;
    private String trainName;
    private TrainType trainType;
    private String trainTypeName;
    private String sourceStationCode;
    private String sourceStationName;
    private String destinationStationCode;
    private String destinationStationName;
    private String departureTime;
    private String arrivalTime;
    private Integer durationMinutes;
    private String durationFormatted;
    private String runsOnDays;
    private String description;
    private Double startingFare;
    private List<ClassAvailabilityDto> classAvailabilities = new ArrayList<>();

    public TrainSearchResultDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public TrainType getTrainType() {
        return trainType;
    }

    public void setTrainType(TrainType trainType) {
        this.trainType = trainType;
        this.trainTypeName = trainType != null ? trainType.name().replace('_', ' ') : "";
    }

    public String getTrainTypeName() {
        return trainTypeName;
    }

    public void setTrainTypeName(String trainTypeName) {
        this.trainTypeName = trainTypeName;
    }

    public String getSourceStationCode() {
        return sourceStationCode;
    }

    public void setSourceStationCode(String sourceStationCode) {
        this.sourceStationCode = sourceStationCode;
    }

    public String getSourceStationName() {
        return sourceStationName;
    }

    public void setSourceStationName(String sourceStationName) {
        this.sourceStationName = sourceStationName;
    }

    public String getDestinationStationCode() {
        return destinationStationCode;
    }

    public void setDestinationStationCode(String destinationStationCode) {
        this.destinationStationCode = destinationStationCode;
    }

    public String getDestinationStationName() {
        return destinationStationName;
    }

    public void setDestinationStationName(String destinationStationName) {
        this.destinationStationName = destinationStationName;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
        if (durationMinutes != null) {
            int hours = durationMinutes / 60;
            int mins = durationMinutes % 60;
            this.durationFormatted = hours + "h " + mins + "m";
        }
    }

    public String getDurationFormatted() {
        return durationFormatted;
    }

    public void setDurationFormatted(String durationFormatted) {
        this.durationFormatted = durationFormatted;
    }

    public String getRunsOnDays() {
        return runsOnDays;
    }

    public void setRunsOnDays(String runsOnDays) {
        this.runsOnDays = runsOnDays;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getStartingFare() {
        return startingFare;
    }

    public void setStartingFare(Double startingFare) {
        this.startingFare = startingFare;
    }

    public List<ClassAvailabilityDto> getClassAvailabilities() {
        return classAvailabilities;
    }

    public void setClassAvailabilities(List<ClassAvailabilityDto> classAvailabilities) {
        this.classAvailabilities = classAvailabilities;
    }
}
