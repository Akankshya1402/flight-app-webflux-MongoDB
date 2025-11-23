package com.flightapp.webfluxmongo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "ticket")
public class Ticket {
    @Id
    private String id;
    private String pnr;
    private String flightId;
    private String email;
    private List<Passenger> passengers;
    private List<String> seatNumbers;
    private String meal; // "VEG" / "NON_VEG" / "NONE"
    private double totalPrice;
    private LocalDateTime bookingTime;
    private String status; // BOOKED, CANCELLED

    public Ticket() {}

    public Ticket(String id, String pnr, String flightId, String email,
                  List<Passenger> passengers, List<String> seatNumbers, String meal,
                  double totalPrice, LocalDateTime bookingTime, String status) {
        this.id = id;
        this.pnr = pnr;
        this.flightId = flightId;
        this.email = email;
        this.passengers = passengers;
        this.seatNumbers = seatNumbers;
        this.meal = meal;
        this.totalPrice = totalPrice;
        this.bookingTime = bookingTime;
        this.status = status;
    }

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }
    public String getFlightId() { return flightId; }
    public void setFlightId(String flightId) { this.flightId = flightId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Passenger> getPassengers() { return passengers; }
    public void setPassengers(List<Passenger> passengers) { this.passengers = passengers; }
    public List<String> getSeatNumbers() { return seatNumbers; }
    public void setSeatNumbers(List<String> seatNumbers) { this.seatNumbers = seatNumbers; }
    public String getMeal() { return meal; }
    public void setMeal(String meal) { this.meal = meal; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public LocalDateTime getBookingTime() { return bookingTime; }
    public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

