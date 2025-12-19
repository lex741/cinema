package com.lex.cinema.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lex.cinema.api.dto.MovieSessionCreateRequest;
import com.lex.cinema.api.dto.MovieSessionUpdateRequest;
import com.lex.cinema.api.dto.PagedResponse;
import com.lex.cinema.exception.BadRequestException;
import com.lex.cinema.model.MovieSession;
import com.lex.cinema.service.MovieSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@Tag(name = "Movie Sessions", description = "CRUD для сеансів кінотеатру")
public class MovieSessionApiController {

    private final MovieSessionService sessionService;
    private final ObjectMapper objectMapper;

    public MovieSessionApiController(MovieSessionService sessionService, ObjectMapper objectMapper) {
        this.sessionService = sessionService;
        this.objectMapper = objectMapper;
    }

    @Operation(
            summary = "Список сеансів (фільтрація + пагінація)",
            description = "Повертає сторінку сеансів. Підтримує фільтри title/hall/from/to та пагінацію page/size."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @GetMapping
    public PagedResponse<MovieSession> list(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String hall,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (page < 0 || size <= 0 || size > 100) throw new BadRequestException("Invalid page/size");

        List<MovieSession> filtered = sessionService.list().stream()
                .filter(s -> title == null || (s.getMovieTitle() != null && s.getMovieTitle().toLowerCase().contains(title.toLowerCase())))
                .filter(s -> hall == null || (s.getHall() != null && s.getHall().equalsIgnoreCase(hall)))
                .filter(s -> from == null || (s.getStartTime() != null && !s.getStartTime().isBefore(from)))
                .filter(s -> to == null || (s.getStartTime() != null && !s.getStartTime().isAfter(to)))
                .sorted(Comparator.comparing(MovieSession::getStartTime))
                .toList();

        long total = filtered.size();
        int fromIdx = Math.min(page * size, (int) total);
        int toIdx = Math.min(fromIdx + size, (int) total);

        List<MovieSession> items = filtered.subList(fromIdx, toIdx);
        return new PagedResponse<>(items, page, size, total);
    }

    @Operation(summary = "Отримати сеанс", description = "Повертає сеанс за id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{id}")
    public MovieSession get(@PathVariable Long id) {
        return sessionService.getOrThrow(id);
    }

    @Operation(summary = "Створити сеанс", description = "Створює сеанс і генерує місця (rows, seatsPerRow).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping
    public ResponseEntity<MovieSession> create(@RequestBody MovieSessionCreateRequest req) {
        if (req.getRows() == null || req.getSeatsPerRow() == null || req.getRows() <= 0 || req.getSeatsPerRow() <= 0) {
            throw new BadRequestException("rows and seatsPerRow must be > 0");
        }

        MovieSession s = new MovieSession();
        s.setMovieTitle(req.getMovieTitle());
        s.setHall(req.getHall());
        s.setStartTime(req.getStartTime());
        s.setPrice(req.getPrice());

        MovieSession saved = sessionService.create(s, req.getRows(), req.getSeatsPerRow());
        return ResponseEntity.created(URI.create("/api/sessions/" + saved.getId())).body(saved);
    }

    @Operation(summary = "Оновити сеанс (PUT)", description = "Повне оновлення полів movieTitle/hall/startTime/price. Місця не перегенеровуються.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PutMapping("/{id}")
    public MovieSession update(@PathVariable Long id, @RequestBody MovieSessionUpdateRequest req) {
        MovieSession existing = sessionService.getOrThrow(id);
        existing.setMovieTitle(req.getMovieTitle());
        existing.setHall(req.getHall());
        existing.setStartTime(req.getStartTime());
        existing.setPrice(req.getPrice());
        return sessionService.update(existing);
    }

    @Operation(
            summary = "Часткове оновлення (PATCH RFC 7386)",
            description = "JSON Merge Patch. Content-Type: application/merge-patch+json. Приклад: {\"price\":210}"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public MovieSession patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws Exception {
        MovieSession existing = sessionService.getOrThrow(id);

        // копія для merge-update
        MovieSession copy = objectMapper.readValue(objectMapper.writeValueAsBytes(existing), MovieSession.class);

        // merge patch
        objectMapper.readerForUpdating(copy).readValue(objectMapper.writeValueAsBytes(patchNode));

        // Забороняємо міняти id та seats через patch
        copy.setId(existing.getId());
        copy.setSeats(existing.getSeats());

        return sessionService.update(copy);
    }

    @Operation(summary = "Видалити сеанс", description = "Видаляє сеанс за id.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sessionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
