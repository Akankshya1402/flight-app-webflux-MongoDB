
package com.flightapp.webfluxmongo.service.impl;

import com.flightapp.webfluxmongo.dto.FlightSearchRequest;
import com.flightapp.webfluxmongo.entity.Airline;
import com.flightapp.webfluxmongo.entity.Flight;
import com.flightapp.webfluxmongo.exception.ResourceNotFoundException;
import com.flightapp.webfluxmongo.repository.AirlineRepository;
import com.flightapp.webfluxmongo.repository.FlightRepository;
import com.flightapp.webfluxmongo.service.FlightService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class FlightServiceImpl implements FlightService {

    private final AirlineRepository airlineRepository;
    private final FlightRepository flightRepository;

    public FlightServiceImpl(AirlineRepository airlineRepository,
                             FlightRepository flightRepository) {
        this.airlineRepository = airlineRepository;
        this.flightRepository = flightRepository;
    }

    @Override
    public Mono<Airline> addAirline(Airline airline) {
        return airlineRepository.save(airline);
    }

    @Override
    public Mono<Flight> addInventory(Flight flight) {
        // validate airline exists
        return airlineRepository.findById(flight.getAirlineId())
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("Airline not found: " + flight.getAirlineId())
                ))
                .flatMap(airline -> {
                    flight.setAirlineName(airline.getName());
                    flight.setAirlineLogoUrl(airline.getLogoUrl());
                    if (flight.getAvailableSeats() == 0) {
                        flight.setAvailableSeats(flight.getTotalSeats());
                    }
                    return flightRepository.save(flight);
                });
    }

    @Override
    public Flux<Flight> searchFlights(FlightSearchRequest request) {
        LocalDate date = request.getJourneyDate();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        return flightRepository.findByFromLocationAndToLocationAndDepartureTimeBetween(
                request.getFromLocation(),
                request.getToLocation(),
                start,
                end
        );
    }

    @Override
    public Mono<Flight> getFlightById(String id) {
        return flightRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("Flight not found: " + id)
                ));
    }
}
