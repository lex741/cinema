package com.lex.cinema.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lex.cinema.model.MovieSession;
import com.lex.cinema.service.MovieSessionService;
import com.lex.cinema.web.dto.MovieSessionCreateRequest;
import com.lex.cinema.web.dto.MovieSessionUpdateRequest;
import com.lex.cinema.web.dto.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Iterator;

@RestController
@RequestMapping("/api/sessions")
@Tag(name = "Sessions", description = "CRUD, фільтрація/пагінація, PATCH RFC 7386")
public class SessionsRestController {

    private final MovieSessionService service;
    private final ObjectMapper mapper;

    public SessionsRestController(MovieSessionService service, ObjectMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "Список сеансів", description = "Фільтрація (title/hall/from/to) + пагінація (page/size).")
    public PagedResponse<MovieSession> list(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String hall,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var items = service.list(title, hall, from, to, page, size);
        var total = service.count(title, hall, from, to);
        return new PagedResponse<>(items, page, size, total);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Отримати сеанс", description = "Повертає сеанс разом зі списком місць.")
    public MovieSession get(@PathVariable long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Створити сеанс", description = "Створює сеанс і генерує місця (rows, seatsPerRow).")
    public MovieSession create(@RequestBody MovieSessionCreateRequest req) {
        MovieSession s = new MovieSession();
        s.setMovieTitle(req.movieTitle);
        s.setHall(req.hall);
        s.setStartTime(req.startTime);
        s.setPrice(req.price);
        return service.create(s, req.rows, req.seatsPerRow);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Оновити сеанс (PUT)", description = "Повне оновлення полів. Місця не перегенеровуються.")
    public MovieSession update(@PathVariable long id, @RequestBody MovieSessionUpdateRequest req) {
        MovieSession s = new MovieSession();
        s.setMovieTitle(req.movieTitle);
        s.setHall(req.hall);
        s.setStartTime(req.startTime);
        s.setPrice(req.price);
        return service.update(id, s);
    }

    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    @Operation(summary = "PATCH RFC 7386", description = "JSON Merge Patch: application/merge-patch+json")
    public MovieSession patch(@PathVariable long id, @RequestBody JsonNode patch) {
        MovieSession current = service.get(id);

        // застосовуємо тільки ті поля, що прийшли
        if (patch.has("movieTitle")) current.setMovieTitle(patch.get("movieTitle").asText());
        if (patch.has("hall")) current.setHall(patch.get("hall").asText());
        if (patch.has("startTime")) current.setStartTime(LocalDateTime.parse(patch.get("startTime").asText()));
        if (patch.has("price")) current.setPrice(patch.get("price").decimalValue());

        MovieSession s = new MovieSession();
        s.setMovieTitle(current.getMovieTitle());
        s.setHall(current.getHall());
        s.setStartTime(current.getStartTime());
        s.setPrice(current.getPrice());

        return service.update(id, s);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Видалити сеанс", description = "DELETE /api/sessions/{id} -> 204")
    public void delete(@PathVariable long id) {
        service.delete(id);
    }
}
