package com.flightapp.webfluxmongo.service.impl;

import com.flightapp.webfluxmongo.dto.FlightSearchRequest;
import com.flightapp.webfluxmongo.entity.Airline;
import com.flightapp.webfluxmongo.entity.Flight;
import com.flightapp.webfluxmongo.exception.ResourceNotFoundException;
import com.flightapp.webfluxmongo.repository.AirlineRepository;
import com.flightapp.webfluxmongo.repository.FlightRepository;
import com.flightapp.webfluxmongo.service.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class FlightServiceImplTest {

    private AirlineRepository airlineRepository;
    private FlightRepository flightRepository;
    private FlightService flightService;

    @BeforeEach
    void setUp() {
        airlineRepository = Mockito.mock(AirlineRepository.class);
        flightRepository = Mockito.mock(FlightRepository.class);
        flightService = new FlightServiceImpl(airlineRepository, flightRepository);
    }

    @Test
    void addAirline_savesAndReturns() {
        Airline input = new Airline(null, "Indigo", "logo.png", true);
        Airline saved = new Airline("A1", "Indigo", "logo.png", true);

        Mockito.when(airlineRepository.save(any(Airline.class)))
                .thenReturn(Mono.just(saved));

        StepVerifier.create(flightService.addAirline(input))
                .expectNextMatches(a ->
                        a.getId().equals("A1") &&
                        a.getName().equals("Indigo"))
                .verifyComplete();
    }

    @Test
    void addInventory_airlineExists_savesFlight() {
        Airline airline = new Airline("A1", "Indigo", "logo.png", true);

        Flight toSave = new Flight(
                null,
                "6E-101",
                "DEL",
                "BOM",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                4500,
                180,
                0,
                "A1",
                null,
                null
        );

        Flight saved = new Flight(
                "F1",
                "6E-101",
                "DEL",
                "BOM",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                4500,
                180,
                180, // available seats should be set from totalSeats
                "A1",
                "Indigo",
                "logo.png"
        );

        Mockito.when(airlineRepository.findById("A1"))
                .thenReturn(Mono.just(airline));

        Mockito.when(flightRepository.save(any(Flight.class)))
                .thenReturn(Mono.just(saved));

        StepVerifier.create(flightService.addInventory(toSave))
                .expectNextMatches(f ->
                        f.getId().equals("F1") &&
                        f.getAirlineName().equals("Indigo") &&
                        f.getAvailableSeats() == 180)
                .verifyComplete();
    }

    @Test
    void addInventory_airlineMissing_throws() {
        Flight flight = new Flight();
        flight.setAirlineId("NON_EXISTENT");

        Mockito.when(airlineRepository.findById("NON_EXISTENT"))
                .thenReturn(Mono.empty());

        StepVerifier.create(flightService.addInventory(flight))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void searchFlights_returnsResults() {
        LocalDate d = LocalDate.now();
        LocalDateTime start = d.atStartOfDay();
        LocalDateTime end = d.atTime(23, 59, 59);

        Flight f = new Flight();
        f.setId("F1");
        f.setFromLocation("DEL");
        f.setToLocation("BOM");
        f.setDepartureTime(LocalDateTime.now());

        Mockito.when(flightRepository.findByFromLocationAndToLocationAndDepartureTimeBetween(
                eq("DEL"), eq("BOM"), eq(start), eq(end)))
                .thenReturn(Flux.fromIterable(Collections.singletonList(f)));

        FlightSearchRequest req = new FlightSearchRequest();
        req.setFromLocation("DEL");
        req.setToLocation("BOM");
        req.setJourneyDate(d);

        StepVerifier.create(flightService.searchFlights(req))
                .expectNextMatches(fl -> fl.getId().equals("F1"))
                .verifyComplete();
    }

    @Test
    void getFlightById_found() {
        Flight f = new Flight();
        f.setId("F1");

        Mockito.when(flightRepository.findById("F1"))
                .thenReturn(Mono.just(f));

        StepVerifier.create(flightService.getFlightById("F1"))
                .expectNextMatches(fl -> fl.getId().equals("F1"))
                .verifyComplete();
    }

    @Test
    void getFlightById_notFound_throws() {
        Mockito.when(flightRepository.findById("X"))
                .thenReturn(Mono.empty());

        StepVerifier.create(flightService.getFlightById("X"))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }
}
