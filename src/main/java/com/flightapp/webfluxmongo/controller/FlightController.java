package com.flightapp.webfluxmongo.controller;

import com.flightapp.webfluxmongo.dto.FlightSearchRequest;
import com.flightapp.webfluxmongo.entity.Airline;
import com.flightapp.webfluxmongo.entity.Flight;
import com.flightapp.webfluxmongo.service.FlightService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1.0/flight")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    // Optional: add airline (admin)
    @PostMapping("/airline")
    public Mono<ResponseEntity<Airline>> addAirline(@RequestBody Airline airline) {
        return flightService.addAirline(airline)
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved));
    }

    // POST /api/v1.0/flight/airline/inventory  -> add inventory/schedule
    @PostMapping("/airline/inventory")
    public Mono<ResponseEntity<Flight>> addInventory(@RequestBody Flight flight) {
        return flightService.addInventory(flight)
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved));
    }

    // POST /api/v1.0/flight/search -> search flights
    @PostMapping("/search")
    public Flux<Flight> searchFlights(@RequestBody FlightSearchRequest request) {
        return flightService.searchFlights(request);
    }
}


