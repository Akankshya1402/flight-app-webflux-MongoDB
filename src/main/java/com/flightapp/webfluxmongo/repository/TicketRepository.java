package com.flightapp.webfluxmongo.repository;

import com.flightapp.webfluxmongo.entity.Ticket;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TicketRepository extends ReactiveMongoRepository<Ticket, String> {

    Mono<Ticket> findByPnr(String pnr);

    Flux<Ticket> findByEmail(String email);
}
