package com.flightapp.webfluxmongo.repository;

import com.flightapp.webfluxmongo.entity.Airline;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface AirlineRepository extends ReactiveMongoRepository<Airline, String> {
}

