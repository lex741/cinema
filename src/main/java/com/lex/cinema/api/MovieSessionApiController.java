package com.lex.cinema.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.lex.cinema.api.dto.MovieSessionCreateRequest;
import com.lex.cinema.api.dto.MovieSessionUpdateRequest;
import com.lex.cinema.api.dto.PagedResponse;
import com.lex.cinema.model.MovieSession;
import com.lex.cinema.service.MovieSessionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api2/sessions")
public class MovieSessionApiController {

    private final MovieSessionService sessionService;

    public MovieSessionApiController(MovieSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping
    public PagedResponse<MovieSession> list(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String hall,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        page = Math.max(page, 0);
        size = Math.min(Math.max(size, 1), 200);

        List<MovieSession> items = sessionService.list(title, hall, from, to, page, size);
        long total = sessionService.count(title, hall, from, to);
        return new PagedResponse<>(items, page, size, total);
    }

    @GetMapping("/{id}")
    public MovieSession get(@PathVariable long id) {
        return sessionService.get(id);
    }

    @PostMapping
    public ResponseEntity<MovieSession> create(@RequestBody MovieSessionCreateRequest req) {
        int rows = (req.getRows() == null) ? 5 : req.getRows();
        int seatsPerRow = (req.getSeatsPerRow() == null) ? 8 : req.getSeatsPerRow();

        MovieSession s = new MovieSession();
        s.setMovieTitle(req.getMovieTitle());
        s.setHall(req.getHall());
        s.setStartTime(req.getStartTime());
        s.setPrice(req.getPrice());

        MovieSession created = sessionService.create(s, rows, seatsPerRow);
        return ResponseEntity.created(URI.create("/api2/sessions/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public MovieSession update(@PathVariable long id, @RequestBody MovieSessionUpdateRequest req) {
        MovieSession s = new MovieSession();
        s.setMovieTitle(req.getMovieTitle());
        s.setHall(req.getHall());
        s.setStartTime(req.getStartTime());
        s.setPrice(req.getPrice());
        return sessionService.update(id, s);
    }

    // RFC 7386 (merge-patch)
    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public MovieSession patch(@PathVariable long id, @RequestBody JsonNode patch) {
        MovieSession current = sessionService.get(id);

        MovieSession s = new MovieSession();
        s.setMovieTitle(current.getMovieTitle());
        s.setHall(current.getHall());
        s.setStartTime(current.getStartTime());
        s.setPrice(current.getPrice());

        if (patch.has("movieTitle") && !patch.get("movieTitle").isNull()) {
            s.setMovieTitle(patch.get("movieTitle").asText());
        }
        if (patch.has("hall") && !patch.get("hall").isNull()) {
            s.setHall(patch.get("hall").asText());
        }
        if (patch.has("startTime") && !patch.get("startTime").isNull()) {
            s.setStartTime(LocalDateTime.parse(patch.get("startTime").asText()));
        }
        if (patch.has("price") && !patch.get("price").isNull()) {
            BigDecimal price = patch.get("price").decimalValue();
            s.setPrice(price);
        }

        return sessionService.update(id, s);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        sessionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
