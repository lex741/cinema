package com.lex.cinema.repository;

import com.lex.cinema.model.MovieSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovieSessionDao {
    long create(MovieSession s);
    Optional<MovieSession> findById(long id);
    List<MovieSession> findAll(String title, String hall, LocalDateTime from, LocalDateTime to, int page, int size);
    long countAll(String title, String hall, LocalDateTime from, LocalDateTime to);
    boolean update(MovieSession s);
    boolean deleteById(long id);
}
