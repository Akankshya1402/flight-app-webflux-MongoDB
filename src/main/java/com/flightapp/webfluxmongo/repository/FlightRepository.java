package com.flightapp.webfluxmongo.repository;

import com.flightapp.webfluxmongo.entity.Flight;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface FlightRepository extends ReactiveMongoRepository<Flight, String> {

    Flux<Flight> findByFromLocationAndToLocationAndDepartureTimeBetween(
            String fromLocation,
            String toLocation,
            LocalDateTime start,
            LocalDateTime end
    );
}
