package com.lex.cinema.repository;

import com.lex.cinema.model.MovieSession;

import java.util.List;
import java.util.Optional;

public interface MovieSessionRepository {
    List<MovieSession> findAll();
    Optional<MovieSession> findById(Long id);
    MovieSession save(MovieSession session);
    void deleteById(Long id);
}
