package com.lex.cinema.repository;

import com.lex.cinema.model.Seat;

import java.util.List;

public interface SeatDao {
    void createForSession(long sessionId, int rows, int seatsPerRow);
    List<Seat> findBySessionId(long sessionId);

    // для транзакційного бронювання: блокуємо місця
    List<Seat> findByIdsForUpdate(List<Long> seatIds);
    int setReserved(List<Long> seatIds, boolean reserved);
}
