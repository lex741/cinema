package com.lex.cinema.service;

import com.lex.cinema.model.MovieSession;

import java.time.LocalDateTime;
import java.util.List;

public interface MovieSessionService {

    // --- новые методы (ЛР5) ---
    MovieSession get(long id);

    List<MovieSession> list(String title,
                            String hall,
                            LocalDateTime from,
                            LocalDateTime to,
                            int page,
                            int size);

    long count(String title, String hall, LocalDateTime from, LocalDateTime to);

    MovieSession create(MovieSession s, int rows, int seatsPerRow);

    MovieSession update(long id, MovieSession s);

    void delete(long id);

    // --- сумісність зі старим контролером (щоб зібралось без правок контролера) ---
    default List<MovieSession> list() {
        return list(null, null, null, null, 0, 100);
    }

    default MovieSession getOrThrow(Long id) {
        if (id == null) throw new IllegalArgumentException("id is null");
        return get(id);
    }

    default MovieSession update(MovieSession s) {
        if (s == null || s.getId() == null) {
            throw new IllegalArgumentException("session/id is null");
        }
        return update(s.getId(), s);
    }
}
