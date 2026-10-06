package com.railnova.dto;

import com.railnova.entity.Booking;
import com.railnova.entity.BookingStatus;
import com.railnova.entity.CoachType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookingResponseDto {
    private Long id;
    private String bookingReference;
    private String pnrNumber;
    private Long userId;
    private String userEmail;
    private String userName;

    private Long trainId;
    private String trainNumber;
    private String trainName;
    private String trainTypeName;

    private String sourceStationCode;
    private String sourceStationName;
    private String destinationStationCode;
    private String destinationStationName;

    private String departureTime;
    private String arrivalTime;
    private LocalDate journeyDate;
    private CoachType coachType;
    private String coachTypeName;

    private Integer totalPassengers;
    private Double baseAmount;
    private Double taxAmount;
    private Double serviceCharge;
    private Double totalAmount;

    private BookingStatus bookingStatus;
    private LocalDateTime bookingTime;

    private List<PassengerResponseDto> passengers = new ArrayList<>();

    private String paymentId;
    private String paymentStatus;

    public BookingResponseDto() {
    }

    public BookingResponseDto(Booking b) {
        if (b != null) {
            this.id = b.getId();
            this.bookingReference = b.getBookingReference();
            this.pnrNumber = b.getPNRNumber();
            if (b.getUser() != null) {
                this.userId = b.getUser().getId();
                this.userEmail = b.getUser().getEmail();
                this.userName = b.getUser().getFullName();
            }
            if (b.getTrain() != null) {
                this.trainId = b.getTrain().getId();
                this.trainNumber = b.getTrain().getTrainNumber();
                this.trainName = b.getTrain().getTrainName();
                this.trainTypeName = b.getTrain().getTrainType() != null ? b.getTrain().getTrainType().name().replace('_', ' ') : "";
                this.departureTime = b.getTrain().getDepartureTime();
                this.arrivalTime = b.getTrain().getArrivalTime();
            }
            if (b.getSourceStation() != null) {
                this.sourceStationCode = b.getSourceStation().getCode();
                this.sourceStationName = b.getSourceStation().getName();
            }
            if (b.getDestinationStation() != null) {
                this.destinationStationCode = b.getDestinationStation().getCode();
                this.destinationStationName = b.getDestinationStation().getName();
            }
            this.journeyDate = b.getJourneyDate();
            this.coachType = b.getCoachType();
            this.coachTypeName = b.getCoachType() != null ? b.getCoachType().getDisplayName() : "";
            this.totalPassengers = b.getTotalPassengers();
            this.baseAmount = b.getBaseAmount();
            this.taxAmount = b.getTaxAmount();
            this.serviceCharge = b.getServiceCharge();
            this.totalAmount = b.getTotalAmount();
            this.bookingStatus = b.getBookingStatus();
            this.bookingTime = b.getBookingTime();

            if (b.getPassengers() != null) {
                this.passengers = b.getPassengers().stream()
                        .map(PassengerResponseDto::new)
                        .collect(Collectors.toList());
            }

            if (b.getPayment() != null) {
                this.paymentId = b.getPayment().getPaymentId();
                this.paymentStatus = b.getPayment().getStatus() != null ? b.getPayment().getStatus().name() : "";
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public String getPnrNumber() {
        return pnrNumber;
    }

    public void setPnrNumber(String pnrNumber) {
        this.pnrNumber = pnrNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getTrainId() {
        return trainId;
    }

    public void setTrainId(Long trainId) {
        this.trainId = trainId;
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

    public String getCoachTypeName() {
        return coachTypeName;
    }

    public void setCoachTypeName(String coachTypeName) {
        this.coachTypeName = coachTypeName;
    }

    public Integer getTotalPassengers() {
        return totalPassengers;
    }

    public void setTotalPassengers(Integer totalPassengers) {
        this.totalPassengers = totalPassengers;
    }

    public Double getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(Double baseAmount) {
        this.baseAmount = baseAmount;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Double getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(Double serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public List<PassengerResponseDto> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PassengerResponseDto> passengers) {
        this.passengers = passengers;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
