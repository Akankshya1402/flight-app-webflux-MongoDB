package com.flightapp.webfluxmongo.service.impl;

import com.flightapp.webfluxmongo.dto.BookingRequest;
import com.flightapp.webfluxmongo.entity.Flight;
import com.flightapp.webfluxmongo.entity.Ticket;
import com.flightapp.webfluxmongo.exception.ResourceNotFoundException;
import com.flightapp.webfluxmongo.repository.FlightRepository;
import com.flightapp.webfluxmongo.repository.TicketRepository;
import com.flightapp.webfluxmongo.service.TicketService;
import com.flightapp.webfluxmongo.util.PnrGenerator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final FlightRepository flightRepository;
    private final PnrGenerator pnrGenerator;

    public TicketServiceImpl(TicketRepository ticketRepository,
                             FlightRepository flightRepository,
                             PnrGenerator pnrGenerator) {
        this.ticketRepository = ticketRepository;
        this.flightRepository = flightRepository;
        this.pnrGenerator = pnrGenerator;
    }

    @Override
    public Mono<Ticket> bookTicket(String flightId, BookingRequest request) {

        return flightRepository.findById(flightId)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("Flight not found: " + flightId)
                ))
                .flatMap(flight -> {

                    int seatsRequested = request.getNumberOfSeats();

                    if (request.getPassengers() == null ||
                            request.getPassengers().size() != seatsRequested) {
                        return Mono.error(new IllegalArgumentException(
                                "numberOfSeats must equal passengers.size()"));
                    }

                    if (request.getSeatNumbers() == null ||
                            request.getSeatNumbers().size() != seatsRequested) {
                        return Mono.error(new IllegalArgumentException(
                                "Seat numbers must be provided for all passengers"));
                    }

                    if (flight.getAvailableSeats() < seatsRequested) {
                        return Mono.error(new IllegalStateException(
                                "Not enough seats available on this flight"));
                    }

                    // update inventory
                    flight.setAvailableSeats(flight.getAvailableSeats() - seatsRequested);

                    String pnr = pnrGenerator.generate();
                    LocalDateTime now = LocalDateTime.now();

                    Ticket ticket = new Ticket();
                    ticket.setPnr(pnr);
                    ticket.setFlightId(flightId);
                    ticket.setEmail(request.getEmail());
                    ticket.setPassengers(request.getPassengers());
                    ticket.setSeatNumbers(request.getSeatNumbers());
                    ticket.setMeal(request.getMeal());
                    ticket.setTotalPrice(seatsRequested * flight.getPrice());
                    ticket.setBookingTime(now);
                    ticket.setStatus("BOOKED");

                    return flightRepository.save(flight)
                            .then(ticketRepository.save(ticket));
                });
    }

    @Override
    public Mono<Ticket> getTicketByPnr(String pnr) {
        return ticketRepository.findByPnr(pnr)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("Ticket not found for PNR: " + pnr)
                ));
    }

    @Override
    public Flux<Ticket> getHistoryByEmail(String email) {
        return ticketRepository.findByEmail(email);
    }

    @Override
    public Mono<Void> cancelTicket(String pnr) {
        return ticketRepository.findByPnr(pnr)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("Ticket not found for PNR: " + pnr)
                ))
                .flatMap(ticket ->
                        flightRepository.findById(ticket.getFlightId())
                                .switchIfEmpty(Mono.error(
                                        new ResourceNotFoundException("Flight not found: " + ticket.getFlightId())
                                ))
                                .flatMap(flight -> {
                                    // 24 hours rule
                                    LocalDateTime cutoff = flight.getDepartureTime().minusHours(24);
                                    if (LocalDateTime.now().isAfter(cutoff)) {
                                        return Mono.error(new IllegalStateException(
                                                "Cannot cancel ticket within 24 hours of departure"));
                                    }

                                    // restore seats
                                    int seats = ticket.getPassengers() != null
                                            ? ticket.getPassengers().size()
                                            : 0;
                                    flight.setAvailableSeats(flight.getAvailableSeats() + seats);

                                    ticket.setStatus("CANCELLED");

                                    return flightRepository.save(flight)
                                            .then(ticketRepository.save(ticket))
                                            .then();
                                })
                );
    }
}


