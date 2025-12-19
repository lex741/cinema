package com.lex.cinema.service.impl;

import com.lex.cinema.model.MovieSession;
import com.lex.cinema.repository.MovieSessionDao;
import com.lex.cinema.repository.SeatDao;
import com.lex.cinema.service.MovieSessionService;
import com.lex.cinema.web.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovieSessionServiceImpl implements MovieSessionService {

    private final MovieSessionDao sessionDao;
    private final SeatDao seatDao;

    public MovieSessionServiceImpl(MovieSessionDao sessionDao, SeatDao seatDao) {
        this.sessionDao = sessionDao;
        this.seatDao = seatDao;
    }

    @Override
    public MovieSession get(long id) {
        MovieSession s = sessionDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Session not found: " + id));
        s.setSeats(seatDao.findBySessionId(id));
        return s;
    }

    @Override
    public List<MovieSession> list(String title, String hall, LocalDateTime from, LocalDateTime to, int page, int size) {
        return sessionDao.findAll(title, hall, from, to, page, size);
    }

    @Override
    public long count(String title, String hall, LocalDateTime from, LocalDateTime to) {
        return sessionDao.countAll(title, hall, from, to);
    }

    @Override
    @Transactional
    public MovieSession create(MovieSession s, int rows, int seatsPerRow) {
        long id = sessionDao.create(s);
        seatDao.createForSession(id, rows, seatsPerRow);
        return get(id);
    }

    @Override
    public MovieSession update(long id, MovieSession s) {
        MovieSession current = get(id);
        current.setMovieTitle(s.getMovieTitle());
        current.setHall(s.getHall());
        current.setStartTime(s.getStartTime());
        current.setPrice(s.getPrice());

        boolean ok = sessionDao.update(current);
        if (!ok) throw new NotFoundException("Session not found: " + id);
        return get(id);
    }

    @Override
    public void delete(long id) {
        boolean ok = sessionDao.deleteById(id);
        if (!ok) throw new NotFoundException("Session not found: " + id);
    }
}
