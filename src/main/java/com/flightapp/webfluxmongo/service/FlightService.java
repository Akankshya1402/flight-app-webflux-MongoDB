package com.flightapp.webfluxmongo.service;

import com.flightapp.webfluxmongo.dto.FlightSearchRequest;
import com.flightapp.webfluxmongo.entity.Airline;
import com.flightapp.webfluxmongo.entity.Flight;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FlightService {

    Mono<Airline> addAirline(Airline airline);

    Mono<Flight> addInventory(Flight flight);

    Flux<Flight> searchFlights(FlightSearchRequest request);

    Mono<Flight> getFlightById(String id);
}
