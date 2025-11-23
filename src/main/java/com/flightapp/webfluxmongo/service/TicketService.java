package com.flightapp.webfluxmongo.service;

import com.flightapp.webfluxmongo.dto.BookingRequest;
import com.flightapp.webfluxmongo.entity.Ticket;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TicketService {

    Mono<Ticket> bookTicket(String flightId, BookingRequest request);

    Mono<Ticket> getTicketByPnr(String pnr);

    Flux<Ticket> getHistoryByEmail(String email);

    Mono<Void> cancelTicket(String pnr);
}
