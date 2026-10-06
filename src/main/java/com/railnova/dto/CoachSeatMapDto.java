package com.railnova.dto;

import com.railnova.entity.CoachType;
import java.util.ArrayList;
import java.util.List;

public class CoachSeatMapDto {
    private Long coachId;
    private String coachCode;
    private CoachType coachType;
    private String coachTypeName;
    private Integer totalSeats;
    private int availableCount;
    private int bookedCount;
    private List<SeatDto> seats = new ArrayList<>();

    public CoachSeatMapDto() {
    }

    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
    }

    public String getCoachCode() {
        return coachCode;
    }

    public void setCoachCode(String coachCode) {
        this.coachCode = coachCode;
    }

    public CoachType getCoachType() {
        return coachType;
    }

    public void setCoachType(CoachType coachType) {
        this.coachType = coachType;
        this.coachTypeName = coachType != null ? coachType.getDisplayName() : "";
    }

    public String getCoachTypeName() {
        return coachTypeName;
    }

    public void setCoachTypeName(String coachTypeName) {
        this.coachTypeName = coachTypeName;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(int availableCount) {
        this.availableCount = availableCount;
    }

    public int getBookedCount() {
        return bookedCount;
    }

    public void setBookedCount(int bookedCount) {
        this.bookedCount = bookedCount;
    }

    public List<SeatDto> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatDto> seats) {
        this.seats = seats;
    }
}
