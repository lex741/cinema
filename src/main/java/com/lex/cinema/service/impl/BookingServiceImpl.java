package com.lex.cinema.service.impl;

import com.lex.cinema.model.Reservation;
import com.lex.cinema.model.Seat;
import com.lex.cinema.repository.MovieSessionDao;
import com.lex.cinema.repository.ReservationDao;
import com.lex.cinema.repository.SeatDao;
import com.lex.cinema.service.BookingService;
import com.lex.cinema.web.error.ConflictException;
import com.lex.cinema.web.error.NotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final MovieSessionDao sessionDao;
    private final SeatDao seatDao;
    private final ReservationDao reservationDao;

    public BookingServiceImpl(MovieSessionDao sessionDao, SeatDao seatDao, ReservationDao reservationDao) {
        this.sessionDao = sessionDao;
        this.seatDao = seatDao;
        this.reservationDao = reservationDao;
    }

    @Override
    @Transactional
    public Reservation book(long sessionId, String customerName, List<Long> seatIds) {
        sessionDao.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Session not found: " + sessionId));

        if (seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("seatIds must not be empty");
        }

        // 1) lock seats FOR UPDATE
        List<Seat> seats = seatDao.findByIdsForUpdate(seatIds);
        if (seats.size() != seatIds.size()) {
            throw new NotFoundException("Some seats not found");
        }
        for (Seat s : seats) {
            if (!Objects.equals(s.getSessionId(), sessionId)) {
                throw new IllegalArgumentException("Seat " + s.getId() + " not in session " + sessionId);
            }
            if (s.isReserved()) {
                throw new ConflictException("Seat already reserved: " + s.getId());
            }
        }

        String name = (customerName == null || customerName.isBlank()) ? "Guest" : customerName.trim();

        // 2) create reservation (id from DB)
        long rid = reservationDao.create(sessionId, name);

        // 3) add seats to reservation (може кинути DuplicateKey -> 409)
        try {
            reservationDao.addSeats(rid, seatIds);
        } catch (DuplicateKeyException e) {
            throw new ConflictException("Some seat already booked");
        }

        // 4) mark seats reserved
        seatDao.setReserved(seatIds, true);

        return get(rid);
    }

    @Override
    public Reservation get(long reservationId) {
        return reservationDao.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found: " + reservationId));
    }

    @Override
    public List<Reservation> list(Long sessionId, int page, int size) {
        return reservationDao.findAll(sessionId, page, size);
    }

    @Override
    public long count(Long sessionId) {
        return reservationDao.countAll(sessionId);
    }

    @Override
    @Transactional
    public void cancel(long reservationId) {
        Reservation r = get(reservationId);
        List<Long> seatIds = reservationDao.findSeatIds(reservationId);

        // звільняємо місця
        seatDao.setReserved(seatIds, false);

        boolean ok = reservationDao.deleteById(reservationId);
        if (!ok) throw new NotFoundException("Reservation not found: " + reservationId);
    }
}
