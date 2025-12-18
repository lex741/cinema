package com.lex.cinema.service;

import com.lex.cinema.config.CinemaConfig.OperationContext;
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
    private final ObjectProvider<OperationContext> operationContextProvider; // prototype factory

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
        OperationContext ctx = operationContextProvider.getObject(); // new instance (prototype)
        MovieSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        if (seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("No seats selected. req=" + ctx.getRequestId());
        }

        // перевірка доступності + бронювання
        Map<String, gitSeat> seatMap = new HashMap<>();
        for (Seat seat : session.getSeats()) seatMap.put(seat.getId(), seat);

        for (String seatId : seatIds) {
            Seat seat = seatMap.get(seatId);
            if (seat == null) throw new IllegalArgumentException("Seat not found: " + seatId);
            if (seat.isReserved()) throw new IllegalStateException("Seat already reserved: " + seatId);
        }

        for (String seatId : seatIds) {
            seatMap.get(seatId).setReserved(true);
        }

        // save session state
        sessionRepo.save(session);

        Reservation r = new Reservation();
        r.setSessionId(sessionId);
        r.setCustomerName(normalizeName(customerName));
        r.setSeatIds(new ArrayList<>(seatIds));
        r.setCreatedAt(LocalDateTime.now());
        Reservation saved = reservationRepo.save(r);

        pricingPolicy.total(session.getPrice(), seatIds.size());

        return saved;
    }

    public List<Reservation> listAll() {
        return reservationRepo.findAll();
    }

    public Reservation getOrThrow(Long id) {
        return reservationRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
    }

    public void cancel(Long reservationId) {
        Reservation r = getOrThrow(reservationId);

        MovieSession session = sessionRepo.findById(r.getSessionId())
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + r.getSessionId()));

        Set<String> toFree = new HashSet<>(r.getSeatIds());
        for (Seat seat : session.getSeats()) {
            if (toFree.contains(seat.getId())) seat.setReserved(false);
        }
        sessionRepo.save(session);
        reservationRepo.deleteById(reservationId);
    }

    private String normalizeName(String name) {
        if (name == null) return "Guest";
        String v = name.trim();
        if (v.isBlank()) return "Guest";
        v = v.replace("<", "").replace(">", "");
        if (v.length() > 40) v = v.substring(0, 40);
        return v;
    }
}
