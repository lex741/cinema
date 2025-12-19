package com.lex.cinema.service;

import com.lex.cinema.exception.NotFoundException;
import com.lex.cinema.model.MovieSession;
import com.lex.cinema.model.Seat;
import com.lex.cinema.repository.MovieSessionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MovieSessionService {

    private final MovieSessionRepository repo;

    public MovieSessionService(MovieSessionRepository repo) {
        this.repo = repo;
    }

    public List<MovieSession> list() {
        return repo.findAll();
    }

    public MovieSession getOrThrow(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Session not found: " + id));
    }

    public MovieSession create(MovieSession session, int rows, int seatsPerRow) {
        session.setSeats(generateSeats(rows, seatsPerRow));
        return repo.save(session);
    }

    public MovieSession update(MovieSession session) {
        return repo.save(session);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    private List<Seat> generateSeats(int rows, int seatsPerRow) {
        List<Seat> seats = new ArrayList<>();
        for (int r = 1; r <= rows; r++) {
            for (int s = 1; s <= seatsPerRow; s++) {
                String id = "R" + r + "-S" + s;
                seats.add(new Seat(id, r, s, false));
            }
        }
        return seats;
    }
}
