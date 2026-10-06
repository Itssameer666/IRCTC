package com.railnova.dto;

import com.railnova.entity.BerthType;
import com.railnova.entity.Seat;

public class SeatDto {
    private Long id;
    private Integer seatNumber;
    private BerthType berthType;
    private String berthTypeName;
    private Integer seatRow;
    private String status; // "AVAILABLE", "BOOKED", "SELECTED"

    public SeatDto() {
    }

    public SeatDto(Seat seat, String status) {
        if (seat != null) {
            this.id = seat.getId();
            this.seatNumber = seat.getSeatNumber();
            this.berthType = seat.getBerthType();
            this.berthTypeName = seat.getBerthType() != null ? seat.getBerthType().name().replace('_', ' ') : "";
            this.seatRow = seat.getSeatRow();
            this.status = status;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getBerthTypeName() {
        return berthTypeName;
    }

    public void setBerthTypeName(String berthTypeName) {
        this.berthTypeName = berthTypeName;
    }

    public Integer getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(Integer seatRow) {
        this.seatRow = seatRow;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
