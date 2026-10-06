package com.railnova.dto;

import com.railnova.entity.CoachType;

public class ClassAvailabilityDto {
    private String coachTypeCode;
    private String coachTypeName;
    private CoachType coachType;
    private Double fare;
    private int availableSeats;
    private String status; // "AVAILABLE 42", "RAC 5", "WL 12"

    public ClassAvailabilityDto() {
    }

    public ClassAvailabilityDto(CoachType coachType, Double fare, int availableSeats) {
        this.coachType = coachType;
        this.coachTypeCode = coachType.getCode();
        this.coachTypeName = coachType.getDisplayName();
        this.fare = fare;
        this.availableSeats = availableSeats;
        if (availableSeats > 5) {
            this.status = "AVAILABLE " + availableSeats;
        } else if (availableSeats > 0) {
            this.status = "RAC " + (6 - availableSeats);
        } else {
            this.status = "WL 18";
        }
    }

    public String getCoachTypeCode() {
        return coachTypeCode;
    }

    public void setCoachTypeCode(String coachTypeCode) {
        this.coachTypeCode = coachTypeCode;
    }

    public String getCoachTypeName() {
        return coachTypeName;
    }

    public void setCoachTypeName(String coachTypeName) {
        this.coachTypeName = coachTypeName;
    }

    public CoachType getCoachType() {
        return coachType;
    }

    public void setCoachType(CoachType coachType) {
        this.coachType = coachType;
    }

    public Double getFare() {
        return fare;
    }

    public void setFare(Double fare) {
        this.fare = fare;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
