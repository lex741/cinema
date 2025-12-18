package com.lex.cinema.repository.stub;

import com.lex.cinema.model.MovieSession;
import com.lex.cinema.repository.MovieSessionRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryMovieSessionRepository implements MovieSessionRepository {

    private final Map<Long, MovieSession> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public List<MovieSession> findAll() {
        List<MovieSession> list = new ArrayList<>(storage.values());
        list.sort(Comparator.comparing(MovieSession::getStartTime));
        return list;
    }

    @Override
    public Optional<MovieSession> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public MovieSession save(MovieSession session) {
        if (session.getId() == null) {
            session.setId(seq.incrementAndGet());
        }
        storage.put(session.getId(), session);
        return session;
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }
}
