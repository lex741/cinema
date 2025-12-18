package com.lex.cinema.repository;

import com.lex.cinema.model.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    List<Reservation> findAll();
    Optional<Reservation> findById(Long id);
    List<Reservation> findBySessionId(Long sessionId);
    Reservation save(Reservation reservation);
    void deleteById(Long id);
}
