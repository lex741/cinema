package com.lex.cinema.service;

import com.lex.cinema.config.CinemaConfig.OperationContext;
import com.lex.cinema.exception.BadRequestException;
import com.lex.cinema.exception.ConflictException;
import com.lex.cinema.exception.NotFoundException;
import com.lex.cinema.model.MovieSession;
import com.lex.cinema.model.Reservation;
import com.lex.cinema.model.Seat;
import com.lex.cinema.repository.MovieSessionRepository;
import com.lex.cinema.repository.ReservationRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class BookingService {

    private final MovieSessionRepository sessionRepo;
    private final ReservationRepository reservationRepo;
    private final PricingPolicy pricingPolicy;
    private final ObjectProvider<OperationContext> operationContextProvider;

    public BookingService(
            MovieSessionRepository sessionRepo,
            ReservationRepository reservationRepo,
            PricingPolicy pricingPolicy,
            ObjectProvider<OperationContext> operationContextProvider
    ) {
        this.sessionRepo = sessionRepo;
        this.reservationRepo = reservationRepo;
        this.pricingPolicy = pricingPolicy;
        this.operationContextProvider = operationContextProvider;
    }

    public Reservation book(Long sessionId, String customerName, List<String> seatIds) {
        OperationContext ctx = operationContextProvider.getObject();

        MovieSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Session not found: " + sessionId));

        if (seatIds == null || seatIds.isEmpty()) {
            throw new BadRequestException("No seats selected. req=" + ctx.getRequestId());
        }

        Map<String, Seat> seatMap = new HashMap<>();
        for (Seat seat : session.getSeats()) seatMap.put(seat.getId(), seat);

        for (String seatId : seatIds) {
            Seat seat = seatMap.get(seatId);
            if (seat == null) throw new BadRequestException("Seat not found in session: " + seatId);
            if (seat.isReserved()) throw new ConflictException("Seat already reserved: " + seatId);
        }

        for (String seatId : seatIds) {
            seatMap.get(seatId).setReserved(true);
        }

        sessionRepo.save(session);

        Reservation r = new Reservation();
        r.setSessionId(sessionId);
        r.setCustomerName(customerName == null || customerName.isBlank() ? "Guest" : customerName.trim());
        r.setSeatIds(new ArrayList<>(seatIds));
        r.setCreatedAt(LocalDateTime.now());

        Reservation saved = reservationRepo.save(r);
        pricingPolicy.total(session.getPrice(), seatIds.size());
        return saved;
    }

    public List<Reservation> listAll() {
        return reservationRepo.findAll();
    }

    public List<Reservation> listBySession(Long sessionId) {
        return reservationRepo.findBySessionId(sessionId);
    }

    public Reservation getOrThrow(Long id) {
        return reservationRepo.findById(id).orElseThrow(() -> new NotFoundException("Order not found: " + id));
    }

    public void cancel(Long reservationId) {
        Reservation r = getOrThrow(reservationId);

        MovieSession session = sessionRepo.findById(r.getSessionId())
                .orElseThrow(() -> new NotFoundException("Session not found: " + r.getSessionId()));

        Set<String> toFree = new HashSet<>(r.getSeatIds());
        for (Seat seat : session.getSeats()) {
            if (toFree.contains(seat.getId())) seat.setReserved(false);
        }
        sessionRepo.save(session);
        reservationRepo.deleteById(reservationId);
    }
}
