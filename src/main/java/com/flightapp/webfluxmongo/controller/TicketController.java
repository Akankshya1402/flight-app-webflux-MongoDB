package com.flightapp.webfluxmongo.controller;

import com.flightapp.webfluxmongo.dto.BookingRequest;
import com.flightapp.webfluxmongo.entity.Ticket;
import com.flightapp.webfluxmongo.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1.0/flight")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // POST /api/v1.0/flight/booking/{flightid}  -> book ticket
    @PostMapping("/booking/{flightId}")
    public Mono<ResponseEntity<Ticket>> bookTicket(@PathVariable String flightId,
                                                   @RequestBody BookingRequest request) {
        return ticketService.bookTicket(flightId, request)
                .map(ticket -> ResponseEntity.status(HttpStatus.CREATED).body(ticket));
    }

    // GET /api/v1.0/flight/ticket/{pnr}
    @GetMapping("/ticket/{pnr}")
    public Mono<Ticket> getTicket(@PathVariable String pnr) {
        return ticketService.getTicketByPnr(pnr);
    }

    // GET /api/v1.0/flight/booking/history/{emailId}
    @GetMapping("/booking/history/{emailId}")
    public Flux<Ticket> getHistory(@PathVariable String emailId) {
        return ticketService.getHistoryByEmail(emailId);
    }

    // DELETE /api/v1.0/flight/booking/cancel/{pnr}
    @DeleteMapping("/booking/cancel/{pnr}")
    public Mono<ResponseEntity<Void>> cancel(@PathVariable String pnr) {
        return ticketService.cancelTicket(pnr)
                .thenReturn(ResponseEntity.ok().<Void>build());
    }
}


