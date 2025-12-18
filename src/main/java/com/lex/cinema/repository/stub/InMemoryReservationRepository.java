package com.lex.cinema.repository.stub;

import com.lex.cinema.model.Reservation;
import com.lex.cinema.repository.ReservationRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryReservationRepository implements ReservationRepository {

    private final Map<Long, Reservation> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public List<Reservation> findAll() {
        List<Reservation> list = new ArrayList<>(storage.values());
        list.sort(Comparator.comparing(Reservation::getCreatedAt).reversed());
        return list;
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Reservation> findBySessionId(Long sessionId) {
        List<Reservation> out = new ArrayList<>();
        for (Reservation r : storage.values()) {
            if (Objects.equals(r.getSessionId(), sessionId)) out.add(r);
        }
        out.sort(Comparator.comparing(Reservation::getCreatedAt).reversed());
        return out;
    }

    @Override
    public Reservation save(Reservation reservation) {
        if (reservation.getId() == null) {
            reservation.setId(seq.incrementAndGet());
        }
        storage.put(reservation.getId(), reservation);
        return reservation;
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }
}
