package com.lex.cinema.repository;

import com.lex.cinema.model.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationDao {
    long create(long sessionId, String customerName);
    void addSeats(long reservationId, List<Long> seatIds);
    Optional<Reservation> findById(long id);
    List<Reservation> findAll(Long sessionId, int page, int size);
    long countAll(Long sessionId);
    boolean deleteById(long id);
    List<Long> findSeatIds(long reservationId);
}
