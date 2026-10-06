package com.railnova.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id", nullable = false)
    @JsonIgnore
    private Coach coach;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "berth_type", nullable = false, length = 30)
    private BerthType berthType;

    @Column(name = "seat_row")
    private Integer seatRow;

    public Seat() {
    }

    public Seat(Coach coach, Integer seatNumber, BerthType berthType, Integer seatRow) {
        this.coach = coach;
        this.seatNumber = seatNumber;
        this.berthType = berthType;
        this.seatRow = seatRow;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public BerthType getBerthType() {
        return berthType;
    }

    public void setBerthType(BerthType berthType) {
        this.berthType = berthType;
    }

    public Integer getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(Integer seatRow) {
        this.seatRow = seatRow;
    }
}
