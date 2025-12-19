package com.lex.cinema.service;

import com.lex.cinema.model.Reservation;

import java.util.List;

public interface BookingService {


    Reservation book(long sessionId, String customerName, List<Long> seatIds);
    Reservation get(long reservationId);
    List<Reservation> list(Long sessionId, int page, int size);
    long count(Long sessionId);
    void cancel(long reservationId);


    default List<Reservation> listAll() {
        return list(null, 0, 1000);
    }

    default List<Reservation> listBySession(Long sessionId) {
        return list(sessionId, 0, 1000);
    }

    default Reservation getOrThrow(Long id) {
        if (id == null) throw new IllegalArgumentException("id is null");
        return get(id);
    }
}
