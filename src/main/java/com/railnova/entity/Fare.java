package com.railnova.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "fares")
public class Fare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id", nullable = false)
    @JsonIgnore
    private Train train;

    @Enumerated(EnumType.STRING)
    @Column(name = "coach_type", nullable = false, length = 30)
    private CoachType coachType;

    @Column(name = "base_fare", nullable = false)
    private Double baseFare;

    @Column(name = "reservation_charge", nullable = false)
    private Double reservationCharge = 40.0;

    @Column(name = "superfast_charge", nullable = false)
    private Double superfastCharge = 45.0;

    @Column(name = "gst_percentage", nullable = false)
    private Double gstPercentage = 5.0;

    public Fare() {
    }

    public Fare(Train train, CoachType coachType, Double baseFare, Double reservationCharge,
                Double superfastCharge, Double gstPercentage) {
        this.train = train;
        this.coachType = coachType;
        this.baseFare = baseFare;
        this.reservationCharge = reservationCharge;
        this.superfastCharge = superfastCharge;
        this.gstPercentage = gstPercentage;
    }

    public Double calculateTotalFare(int passengerCount) {
        double subtotal = (baseFare + reservationCharge + superfastCharge) * passengerCount;
        double gst = (subtotal * gstPercentage) / 100.0;
        return subtotal + gst;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public CoachType getCoachType() {
        return coachType;
    }

    public void setCoachType(CoachType coachType) {
        this.coachType = coachType;
    }

    public Double getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(Double baseFare) {
        this.baseFare = baseFare;
    }

    public Double getReservationCharge() {
        return reservationCharge;
    }

    public void setReservationCharge(Double reservationCharge) {
        this.reservationCharge = reservationCharge;
    }

    public Double getSuperfastCharge() {
        return superfastCharge;
    }

    public void setSuperfastCharge(Double superfastCharge) {
        this.superfastCharge = superfastCharge;
    }

    public Double getGstPercentage() {
        return gstPercentage;
    }

    public void setGstPercentage(Double gstPercentage) {
        this.gstPercentage = gstPercentage;
    }
}
