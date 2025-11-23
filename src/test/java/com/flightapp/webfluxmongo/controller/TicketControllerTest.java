package com.flightapp.webfluxmongo.controller;

import com.flightapp.webfluxmongo.dto.BookingRequest;
import com.flightapp.webfluxmongo.entity.Passenger;
import com.flightapp.webfluxmongo.entity.Ticket;
import com.flightapp.webfluxmongo.service.TicketService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@WebFluxTest(controllers = TicketController.class)
class TicketControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TicketService ticketService;

    @Test
    void bookTicket_returns201() {
        String flightId = "F1";

        BookingRequest req = new BookingRequest();
        req.setEmail("user@test.com");
        req.setNumberOfSeats(2);
        req.setPassengers(List.of(
                new Passenger("A", "F", 25),
                new Passenger("B", "M", 26)
        ));
        req.setSeatNumbers(List.of("1A", "1B"));
        req.setMeal("VEG");

        Ticket saved = new Ticket();
        saved.setId("T1");
        saved.setPnr("PNR-12345678");
        saved.setFlightId(flightId);
        saved.setEmail("user@test.com");
        saved.setBookingTime(LocalDateTime.now());
        saved.setStatus("BOOKED");

        Mockito.when(ticketService.bookTicket(eq(flightId), any(BookingRequest.class)))
                .thenReturn(Mono.just(saved));

        webTestClient.post()
                .uri("/api/v1.0/flight/booking/{flightId}", flightId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("T1")
                .jsonPath("$.pnr").isEqualTo("PNR-12345678");
    }

    @Test
    void getTicketByPnr_returnsTicket() {
        Ticket ticket = new Ticket();
        ticket.setId("T1");
        ticket.setPnr("PNR-111");

        Mockito.when(ticketService.getTicketByPnr("PNR-111"))
                .thenReturn(Mono.just(ticket));

        webTestClient.get()
                .uri("/api/v1.0/flight/ticket/{pnr}", "PNR-111")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("T1")
                .jsonPath("$.pnr").isEqualTo("PNR-111");
    }

    @Test
    void getHistoryByEmail_returnsList() {
        Ticket t = new Ticket();
        t.setId("T1");
        t.setEmail("user@test.com");

        Mockito.when(ticketService.getHistoryByEmail("user@test.com"))
                .thenReturn(Flux.just(t));

        webTestClient.get()
                .uri("/api/v1.0/flight/booking/history/{emailId}", "user@test.com")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("T1")
                .jsonPath("$[0].email").isEqualTo("user@test.com");
    }

    @Test
    void cancelTicket_returns200() {
        Mockito.when(ticketService.cancelTicket("PNR-111"))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1.0/flight/booking/cancel/{pnr}", "PNR-111")
                .exchange()
                .expectStatus().isOk();
    }
}
