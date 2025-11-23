package com.flightapp.webfluxmongo.controller;

import com.flightapp.webfluxmongo.dto.FlightSearchRequest;
import com.flightapp.webfluxmongo.entity.Airline;
import com.flightapp.webfluxmongo.entity.Flight;
import com.flightapp.webfluxmongo.service.FlightService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(controllers = FlightController.class)
class FlightControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private FlightService flightService;

    @Test
    void addAirline_returns201() {
        Airline airline = new Airline("A1", "Air India", "logo.png", true);

        Mockito.when(flightService.addAirline(any(Airline.class)))
                .thenReturn(Mono.just(airline));

        webTestClient.post()
                .uri("/api/v1.0/flight/airline")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(airline)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Air India");
    }

    @Test
    void addInventory_returns201() {
        Flight f = new Flight();
        f.setId("F1");
        f.setFlightNumber("AI-101");
        f.setFromLocation("DEL");
        f.setToLocation("BLR");
        f.setDepartureTime(LocalDateTime.now().plusDays(1));
        f.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
        f.setPrice(5000);
        f.setAvailableSeats(100);

        Mockito.when(flightService.addInventory(any(Flight.class)))
                .thenReturn(Mono.just(f));

        webTestClient.post()
                .uri("/api/v1.0/flight/airline/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(f)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.flightNumber").isEqualTo("AI-101");
    }

    @Test
    void searchFlights_returnsList() {
        Flight f = new Flight();
        f.setId("F1");
        f.setFlightNumber("AI-101");
        f.setFromLocation("DEL");
        f.setToLocation("BLR");

        FlightSearchRequest req = new FlightSearchRequest();
        req.setFromLocation("DEL");
        req.setToLocation("BLR");
        req.setJourneyDate(LocalDate.now().plusDays(1));

        Mockito.when(flightService.searchFlights(any()))
                .thenReturn(Flux.just(f));

        webTestClient.post()
                .uri("/api/v1.0/flight/search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Flight.class)
                .hasSize(1);
    }
}

