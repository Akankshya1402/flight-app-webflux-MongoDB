package com.flightapp.webfluxmongo.dto;

import com.flightapp.webfluxmongo.entity.Passenger;
import java.util.List;

public class BookingRequest {

    private String email;
    private int numberOfSeats;
    private List<Passenger> passengers;
    private String meal;             // "VEG" / "NON_VEG" / "NONE"
    private List<String> seatNumbers;

    public BookingRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public List<String> getSeatNumbers() {
        return seatNumbers;
    }

    public void setSeatNumbers(List<String> seatNumbers) {
        this.seatNumbers = seatNumbers;
    }
}

