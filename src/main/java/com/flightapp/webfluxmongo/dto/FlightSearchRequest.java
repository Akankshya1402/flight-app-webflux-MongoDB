package com.flightapp.webfluxmongo.dto;

import java.time.LocalDate;

public class FlightSearchRequest {

    private String fromLocation;
    private String toLocation;
    private LocalDate journeyDate;   // one-way date

    public FlightSearchRequest() {
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

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
    }
}
