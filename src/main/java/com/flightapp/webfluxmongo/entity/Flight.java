package com.flightapp.webfluxmongo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "flight")
public class Flight {

    @Id
    private String id;

    private String flightNumber;
    private String fromLocation;
    private String toLocation;

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    private double price;          // one-way price
    private int totalSeats;
    private int availableSeats;

    private String airlineId;
    private String airlineName;
    private String airlineLogoUrl;

    public Flight() {
    }

    public Flight(String id, String flightNumber, String fromLocation, String toLocation,
                  LocalDateTime departureTime, LocalDateTime arrivalTime,
                  double price, int totalSeats, int availableSeats,
                  String airlineId, String airlineName, String airlineLogoUrl) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.airlineId = airlineId;
        this.airlineName = airlineName;
        this.airlineLogoUrl = airlineLogoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getAirlineId() {
        return airlineId;
    }

    public void setAirlineId(String airlineId) {
        this.airlineId = airlineId;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public String getAirlineLogoUrl() {
        return airlineLogoUrl;
    }

    public void setAirlineLogoUrl(String airlineLogoUrl) {
        this.airlineLogoUrl = airlineLogoUrl;
    }
}
