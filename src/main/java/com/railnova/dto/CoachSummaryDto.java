package com.railnova.dto;

import com.railnova.entity.Coach;
import com.railnova.entity.CoachType;

public class CoachSummaryDto {
    private Long id;
    private String coachCode;
    private CoachType coachType;
    private String coachTypeCode;
    private Integer totalSeats;

    public CoachSummaryDto() {
    }

    public CoachSummaryDto(Coach coach) {
        if (coach != null) {
            this.id = coach.getId();
            this.coachCode = coach.getCoachCode();
            this.coachType = coach.getCoachType();
            this.coachTypeCode = coach.getCoachType() != null ? coach.getCoachType().getCode() : "";
            this.totalSeats = coach.getTotalSeats();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    }

    public String getCoachTypeCode() {
        return coachTypeCode;
    }

    public void setCoachTypeCode(String coachTypeCode) {
        this.coachTypeCode = coachTypeCode;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }
}
