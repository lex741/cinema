package com.lex.cinema.bootstrap;

import com.lex.cinema.model.MovieSession;
import com.lex.cinema.repository.MovieSessionRepository;
import com.lex.cinema.service.MovieSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class SampleDataLoader {

    @Autowired
    private MovieSessionRepository sessionRepository;

    private final MovieSessionService sessionService;

    public SampleDataLoader(MovieSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostConstruct
    public void init() {
        if (!sessionRepository.findAll().isEmpty()) return;

        MovieSession s1 = new MovieSession();
        s1.setMovieTitle("Interstellar");
        s1.setHall("Hall A");
        s1.setStartTime(LocalDateTime.now().plusDays(1).withSecond(0).withNano(0));
        s1.setPrice(new BigDecimal("180"));
        sessionService.create(s1, 6, 10);

        MovieSession s2 = new MovieSession();
        s2.setMovieTitle("Dune");
        s2.setHall("Hall B");
        s2.setStartTime(LocalDateTime.now().plusDays(2).withSecond(0).withNano(0));
        s2.setPrice(new BigDecimal("200"));
        sessionService.create(s2, 5, 8);
    }
}
