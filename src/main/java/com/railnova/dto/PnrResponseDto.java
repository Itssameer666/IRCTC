package com.railnova.dto;

import com.railnova.entity.Booking;
import com.railnova.entity.BookingStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PnrResponseDto {
    private String pnrNumber;
    private String bookingReference;
    private String trainNumber;
    private String trainName;
    private String trainType;
    private String sourceStation;
    private String destinationStation;
    private String departureTime;
    private String arrivalTime;
    private LocalDate journeyDate;
    private String coachType;
    private BookingStatus bookingStatus;
    private Double totalAmount;
    private List<PassengerResponseDto> passengers = new ArrayList<>();

    public PnrResponseDto() {
    }

    public PnrResponseDto(Booking b) {
        if (b != null) {
            this.pnrNumber = b.getPNRNumber();
            this.bookingReference = b.getBookingReference();
            if (b.getTrain() != null) {
                this.trainNumber = b.getTrain().getTrainNumber();
                this.trainName = b.getTrain().getTrainName();
                this.trainType = b.getTrain().getTrainType() != null ? b.getTrain().getTrainType().name() : "";
                this.departureTime = b.getTrain().getDepartureTime();
                this.arrivalTime = b.getTrain().getArrivalTime();
            }
            if (b.getSourceStation() != null) {
                this.sourceStation = b.getSourceStation().getName() + " (" + b.getSourceStation().getCode() + ")";
            }
            if (b.getDestinationStation() != null) {
                this.destinationStation = b.getDestinationStation().getName() + " (" + b.getDestinationStation().getCode() + ")";
            }
            this.journeyDate = b.getJourneyDate();
            this.coachType = b.getCoachType() != null ? b.getCoachType().getDisplayName() : "";
            this.bookingStatus = b.getBookingStatus();
            this.totalAmount = b.getTotalAmount();
            if (b.getPassengers() != null) {
                this.passengers = b.getPassengers().stream()
                        .map(PassengerResponseDto::new)
                        .collect(Collectors.toList());
            }
        }
    }

    public String getPnrNumber() {
        return pnrNumber;
    }

    public void setPnrNumber(String pnrNumber) {
        this.pnrNumber = pnrNumber;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
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

    public String getTrainType() {
        return trainType;
    }

    public void setTrainType(String trainType) {
        this.trainType = trainType;
    }

    public String getSourceStation() {
        return sourceStation;
    }

    public void setSourceStation(String sourceStation) {
        this.sourceStation = sourceStation;
    }

    public String getDestinationStation() {
        return destinationStation;
    }

    public void setDestinationStation(String destinationStation) {
        this.destinationStation = destinationStation;
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

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
    }

    public String getCoachType() {
        return coachType;
    }

    public void setCoachType(String coachType) {
        this.coachType = coachType;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<PassengerResponseDto> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PassengerResponseDto> passengers) {
        this.passengers = passengers;
    }
}
