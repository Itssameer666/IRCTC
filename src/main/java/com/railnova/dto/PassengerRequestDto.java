package com.railnova.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PassengerRequestDto {

    @NotBlank(message = "Passenger name is required")
    private String fullName;

    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age must be at least 1")
    @Max(value = 120, message = "Age must be valid")
    private Integer age;

    @NotBlank(message = "Gender is required")
    private String gender; // MALE, FEMALE, OTHER

    private String berthPreference; // LOWER, MIDDLE, UPPER, SIDE_LOWER, SIDE_UPPER

    private String idType; // Aadhaar, Passport, Voter ID

    private String idNumber;

    private Long selectedSeatId; // optional explicit seat selection

    public PassengerRequestDto() {
    }

    public PassengerRequestDto(String fullName, Integer age, String gender, String berthPreference,
                               String idType, String idNumber, Long selectedSeatId) {
        this.fullName = fullName;
        this.age = age;
        this.gender = gender;
        this.berthPreference = berthPreference;
        this.idType = idType;
        this.idNumber = idNumber;
        this.selectedSeatId = selectedSeatId;
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

    public Long getSelectedSeatId() {
        return selectedSeatId;
    }

    public void setSelectedSeatId(Long selectedSeatId) {
        this.selectedSeatId = selectedSeatId;
    }
}
