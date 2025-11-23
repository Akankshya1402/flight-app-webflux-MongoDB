package com.flightapp.webfluxmongo.service.impl;
import com.flightapp.webfluxmongo.service.impl.TicketServiceImpl;

import com.flightapp.webfluxmongo.dto.BookingRequest;
import com.flightapp.webfluxmongo.entity.Flight;
import com.flightapp.webfluxmongo.entity.Passenger;
import com.flightapp.webfluxmongo.entity.Ticket;
import com.flightapp.webfluxmongo.exception.ResourceNotFoundException;
import com.flightapp.webfluxmongo.repository.FlightRepository;
import com.flightapp.webfluxmongo.repository.TicketRepository;
import com.flightapp.webfluxmongo.service.TicketService;
import com.flightapp.webfluxmongo.util.PnrGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


class TicketServiceImplTest {

    private TicketRepository ticketRepository;
    private FlightRepository flightRepository;
    private PnrGenerator pnrGenerator;
    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        ticketRepository = Mockito.mock(TicketRepository.class);
        flightRepository = Mockito.mock(FlightRepository.class);
        pnrGenerator = Mockito.mock(PnrGenerator.class);

        ticketService = new TicketServiceImpl(
                ticketRepository,
                flightRepository,
                pnrGenerator
        );
    }

    private Flight buildFlight(String id, int availableSeats, double price, LocalDateTime departureTime) {
        Flight f = new Flight();
        f.setId(id);
        f.setAvailableSeats(availableSeats);
        f.setPrice(price);
        f.setDepartureTime(departureTime);
        return f;
    }

    private BookingRequest buildBookingRequest(int seats) {
        BookingRequest req = new BookingRequest();
        req.setEmail("user@test.com");
        req.setNumberOfSeats(seats);
        req.setPassengers(List.of(
                new Passenger("A", "F", 25),
                new Passenger("B", "M", 26)
        ));
        req.setSeatNumbers(List.of("1A", "1B"));
        req.setMeal("VEG");
        return req;
    }

    @Test
    void bookTicket_success() {
        String flightId = "F1";
        LocalDateTime departure = LocalDateTime.now().plusDays(2);
        Flight flight = buildFlight(flightId, 100, 5000, departure);

        BookingRequest req = buildBookingRequest(2);

        when(flightRepository.findById(flightId)).thenReturn(Mono.just(flight));
        when(pnrGenerator.generate()).thenReturn("PNR-12345678");
        when(flightRepository.save(any(Flight.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> {
            Ticket t = inv.getArgument(0);
            t.setId("T1");
            return Mono.just(t);
        });

        StepVerifier.create(ticketService.bookTicket(flightId, req))
                .expectNextMatches(t ->
                        t.getId().equals("T1") &&
                        t.getPnr().equals("PNR-12345678") &&
                        t.getTotalPrice() == (2 * 5000))
                .verifyComplete();
    }

    @Test
    void bookTicket_flightNotFound() {
        BookingRequest req = buildBookingRequest(2);

        when(flightRepository.findById("XXX")).thenReturn(Mono.empty());

        StepVerifier.create(ticketService.bookTicket("XXX", req))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void bookTicket_notEnoughSeats() {
        String flightId = "F1";
        LocalDateTime departure = LocalDateTime.now().plusDays(2);
        Flight flight = buildFlight(flightId, 1, 5000, departure);

        BookingRequest req = buildBookingRequest(2);

        when(flightRepository.findById(flightId)).thenReturn(Mono.just(flight));

        StepVerifier.create(ticketService.bookTicket(flightId, req))
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void bookTicket_mismatchedPassengersCount() {
        String flightId = "F1";
        LocalDateTime departure = LocalDateTime.now().plusDays(2);
        Flight flight = buildFlight(flightId, 100, 5000, departure);

        BookingRequest req = new BookingRequest();
        req.setEmail("user@test.com");
        req.setNumberOfSeats(2);
        req.setPassengers(List.of(new Passenger("A", "F", 25))); // only 1 passenger
        req.setSeatNumbers(List.of("1A", "1B"));
        req.setMeal("VEG");

        when(flightRepository.findById(flightId)).thenReturn(Mono.just(flight));

        StepVerifier.create(ticketService.bookTicket(flightId, req))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getTicketByPnr_found() {
        Ticket t = new Ticket();
        t.setId("T1");
        t.setPnr("PNR-111");

        when(ticketRepository.findByPnr("PNR-111"))
                .thenReturn(Mono.just(t));

        StepVerifier.create(ticketService.getTicketByPnr("PNR-111"))
                .expectNextMatches(ticket -> ticket.getId().equals("T1"))
                .verifyComplete();
    }

    @Test
    void getTicketByPnr_notFound_throws() {
        when(ticketRepository.findByPnr("PNR-XXX"))
                .thenReturn(Mono.empty());

        StepVerifier.create(ticketService.getTicketByPnr("PNR-XXX"))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void getHistoryByEmail_returnsFlux() {
        Ticket t = new Ticket();
        t.setEmail("user@test.com");
        t.setId("T1");

        when(ticketRepository.findByEmail("user@test.com"))
                .thenReturn(Flux.just(t));

        StepVerifier.create(ticketService.getHistoryByEmail("user@test.com"))
                .expectNextMatches(ticket -> ticket.getId().equals("T1"))
                .verifyComplete();
    }

    @Test
    void cancelTicket_success_before24Hours() {
        String pnr = "PNR-OK";
        Ticket ticket = new Ticket();
        ticket.setId("T1");
        ticket.setPnr(pnr);
        ticket.setFlightId("F1");
        ticket.setPassengers(List.of(
                new Passenger("A", "F", 25),
                new Passenger("B", "M", 26)
        ));
        ticket.setStatus("BOOKED");

        LocalDateTime departure = LocalDateTime.now().plusDays(2);
        Flight flight = buildFlight("F1", 10, 5000, departure);

        when(ticketRepository.findByPnr(pnr)).thenReturn(Mono.just(ticket));
        when(flightRepository.findById("F1")).thenReturn(Mono.just(flight));
        when(flightRepository.save(any(Flight.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(ticketService.cancelTicket(pnr))
                .verifyComplete();
    }

    @Test
    void cancelTicket_within24Hours_throws() {
        String pnr = "PNR-LATE";
        Ticket ticket = new Ticket();
        ticket.setId("T1");
        ticket.setPnr(pnr);
        ticket.setFlightId("F1");
        ticket.setPassengers(List.of(
                new Passenger("A", "F", 25)
        ));
        ticket.setStatus("BOOKED");

        // departure in 2 hours -> cutoff = now - 22h => cannot cancel
        LocalDateTime departure = LocalDateTime.now().plusHours(2);
        Flight flight = buildFlight("F1", 10, 5000, departure);

        when(ticketRepository.findByPnr(pnr)).thenReturn(Mono.just(ticket));
        when(flightRepository.findById("F1")).thenReturn(Mono.just(flight));

        StepVerifier.create(ticketService.cancelTicket(pnr))
                .expectError(IllegalStateException.class)
                .verify();
    }
}

