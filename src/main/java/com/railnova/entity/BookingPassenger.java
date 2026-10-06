package com.railnova.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "booking_passengers")
public class BookingPassenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonBackReference
    private Booking booking;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false, length = 10)
    private String gender; // MALE, FEMALE, OTHER

    @Column(name = "berth_preference", length = 30)
    private String berthPreference;

    @Column(name = "assigned_coach", length = 10)
    private String assignedCoach; // e.g. "B1"

    @Column(name = "assigned_seat")
    private Integer assignedSeat; // e.g. 15

    @Column(name = "assigned_berth", length = 30)
    private String assignedBerth; // e.g. "LOWER"

    @Column(name = "id_type", length = 30)
    private String idType; // Aadhaar, Passport, etc.

    @Column(name = "id_number", length = 50)
    private String idNumber;

    public BookingPassenger() {
    }

    public BookingPassenger(Booking booking, String fullName, Integer age, String gender,
                            String berthPreference, String idType, String idNumber) {
        this.booking = booking;
        this.fullName = fullName;
        this.age = age;
        this.gender = gender;
        this.berthPreference = berthPreference;
        this.idType = idType;
        this.idNumber = idNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
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
