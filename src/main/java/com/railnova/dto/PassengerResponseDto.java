package com.railnova.dto;

import com.railnova.entity.BookingPassenger;

public class PassengerResponseDto {
    private Long id;
    private String fullName;
    private Integer age;
    private String gender;
    private String berthPreference;
    private String assignedCoach;
    private Integer assignedSeat;
    private String assignedBerth;
    private String idType;
    private String idNumber;

    public PassengerResponseDto() {
    }

    public PassengerResponseDto(BookingPassenger bp) {
        if (bp != null) {
            this.id = bp.getId();
            this.fullName = bp.getFullName();
            this.age = bp.getAge();
            this.gender = bp.getGender();
            this.berthPreference = bp.getBerthPreference();
            this.assignedCoach = bp.getAssignedCoach();
            this.assignedSeat = bp.getAssignedSeat();
            this.assignedBerth = bp.getAssignedBerth();
            this.idType = bp.getIdType();
            this.idNumber = bp.getIdNumber();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBerthPreference() {
        return berthPreference;
    }

    public void setBerthPreference(String berthPreference) {
        this.berthPreference = berthPreference;
    }

    public String getAssignedCoach() {
        return assignedCoach;
    }

    public void setAssignedCoach(String assignedCoach) {
        this.assignedCoach = assignedCoach;
    }

    public Integer getAssignedSeat() {
        return assignedSeat;
    }

    public void setAssignedSeat(Integer assignedSeat) {
        this.assignedSeat = assignedSeat;
    }

    public String getAssignedBerth() {
        return assignedBerth;
    }

    public void setAssignedBerth(String assignedBerth) {
        this.assignedBerth = assignedBerth;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }
}
