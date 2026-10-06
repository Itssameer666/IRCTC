package com.railnova.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "train_routes")
public class TrainRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id", nullable = false)
    @JsonIgnore
    private Train train;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Column(name = "stop_number", nullable = false)
    private Integer stopNumber;

    @Column(name = "arrival_time", nullable = false, length = 10)
    private String arrivalTime;

    @Column(name = "departure_time", nullable = false, length = 10)
    private String departureTime;

    @Column(name = "distance_km", nullable = false)
    private Integer distanceKm;

    @Column(name = "halt_minutes", nullable = false)
    private Integer haltMinutes;

    @Column(length = 20)
    private String platform;

    public TrainRoute() {
    }

    public TrainRoute(Train train, Station station, Integer stopNumber, String arrivalTime,
                      String departureTime, Integer distanceKm, Integer haltMinutes, String platform) {
        this.train = train;
        this.station = station;
        this.stopNumber = stopNumber;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.distanceKm = distanceKm;
        this.haltMinutes = haltMinutes;
        this.platform = platform;
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

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public Integer getStopNumber() {
        return stopNumber;
    }

    public void setStopNumber(Integer stopNumber) {
        this.stopNumber = stopNumber;
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
