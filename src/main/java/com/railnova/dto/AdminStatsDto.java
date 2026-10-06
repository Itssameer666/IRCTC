package com.railnova.dto;

import java.util.List;
import java.util.Map;

public class AdminStatsDto {
    private long totalUsers;
    private long totalTrains;
    private long todayBookings;
    private long confirmedTickets;
    private long cancelledTickets;
    private Double totalRevenue;
    private long pendingRefunds;

    private List<String> chartLabels;
    private List<Long> chartBookingsData;
    private List<Double> chartRevenueData;
    private Map<String, Long> trainTypeDistribution;

    public AdminStatsDto() {
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalTrains() {
        return totalTrains;
    }

    public void setTotalTrains(long totalTrains) {
        this.totalTrains = totalTrains;
    }

    public long getTodayBookings() {
        return todayBookings;
    }

    public void setTodayBookings(long todayBookings) {
        this.todayBookings = todayBookings;
    }

    public long getConfirmedTickets() {
        return confirmedTickets;
    }

    public void setConfirmedTickets(long confirmedTickets) {
        this.confirmedTickets = confirmedTickets;
    }

    public long getCancelledTickets() {
        return cancelledTickets;
    }

    public void setCancelledTickets(long cancelledTickets) {
        this.cancelledTickets = cancelledTickets;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getPendingRefunds() {
        return pendingRefunds;
    }

    public void setPendingRefunds(long pendingRefunds) {
        this.pendingRefunds = pendingRefunds;
    }

    public List<String> getChartLabels() {
        return chartLabels;
    }

    public void setChartLabels(List<String> chartLabels) {
        this.chartLabels = chartLabels;
    }

    public List<Long> getChartBookingsData() {
        return chartBookingsData;
    }

    public void setChartBookingsData(List<Long> chartBookingsData) {
        this.chartBookingsData = chartBookingsData;
    }

    public List<Double> getChartRevenueData() {
        return chartRevenueData;
    }

    public void setChartRevenueData(List<Double> chartRevenueData) {
        this.chartRevenueData = chartRevenueData;
    }

    public Map<String, Long> getTrainTypeDistribution() {
        return trainTypeDistribution;
    }

    public void setTrainTypeDistribution(Map<String, Long> trainTypeDistribution) {
        this.trainTypeDistribution = trainTypeDistribution;
    }
}
