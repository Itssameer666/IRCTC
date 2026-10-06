package com.railnova.dto;

import com.railnova.entity.TrainRoute;

public class RouteStopDto {
    private Long id;
    private Integer stopNumber;
    private String stationCode;
    private String stationName;
    private String city;
    private String arrivalTime;
    private String departureTime;
    private Integer distanceKm;
    private Integer haltMinutes;
    private String platform;

    public RouteStopDto() {
    }

    public RouteStopDto(TrainRoute tr) {
        if (tr != null) {
            this.id = tr.getId();
            this.stopNumber = tr.getStopNumber();
            if (tr.getStation() != null) {
                this.stationCode = tr.getStation().getCode();
                this.stationName = tr.getStation().getName();
                this.city = tr.getStation().getCity();
            }
            this.arrivalTime = tr.getArrivalTime();
            this.departureTime = tr.getDepartureTime();
            this.distanceKm = tr.getDistanceKm();
            this.haltMinutes = tr.getHaltMinutes();
            this.platform = tr.getPlatform() != null ? tr.getPlatform() : "1";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStopNumber() {
        return stopNumber;
    }

    public void setStopNumber(Integer stopNumber) {
        this.stopNumber = stopNumber;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public Integer getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Integer distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Integer getHaltMinutes() {
        return haltMinutes;
    }

    public void setHaltMinutes(Integer haltMinutes) {
        this.haltMinutes = haltMinutes;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
