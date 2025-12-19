package com.lex.cinema.api;

import com.lex.cinema.api.dto.PagedResponse;
import com.lex.cinema.api.dto.ReservationCreateRequest;
import com.lex.cinema.exception.BadRequestException;
import com.lex.cinema.model.Reservation;
import com.lex.cinema.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservations", description = "Операції бронювання квитків")
public class ReservationApiController {

    private final BookingService bookingService;

    public ReservationApiController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(summary = "Список бронювань (пагінація + фільтр sessionId)",
            description = "Повертає сторінку бронювань. Можна фільтрувати за sessionId.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "OK") })
    @GetMapping
    public PagedResponse<Reservation> list(
            @RequestParam(required = false) Long sessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (page < 0 || size <= 0 || size > 100) throw new BadRequestException("Invalid page/size");

        List<Reservation> all = (sessionId == null) ? bookingService.listAll() : bookingService.listBySession(sessionId);

        long total = all.size();
        int fromIdx = Math.min(page * size, (int) total);
        int toIdx = Math.min(fromIdx + size, (int) total);

        List<Reservation> items = all.subList(fromIdx, toIdx);
        return new PagedResponse<>(items, page, size, total);
    }

    @Operation(summary = "Отримати бронювання", description = "Повертає бронювання за id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{id}")
    public Reservation get(@PathVariable Long id) {
        return bookingService.getOrThrow(id);
    }

    @Operation(summary = "Створити бронювання", description = "Бронює seatIds для sessionId. Повертає 201 або 409/400/404.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "409", description = "Conflict")
    })
    @PostMapping
    public ResponseEntity<Reservation> create(@RequestBody ReservationCreateRequest req) {
        if (req.getSessionId() == null) throw new BadRequestException("sessionId is required");
        Reservation saved = bookingService.book(req.getSessionId(), req.getCustomerName(), req.getSeatIds());
        return ResponseEntity.created(URI.create("/api/reservations/" + saved.getId())).body(saved);
    }

    @Operation(summary = "Скасувати бронювання", description = "Видаляє бронювання та звільняє місця.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookingService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}
