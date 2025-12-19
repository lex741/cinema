package com.lex.cinema.api;

import com.lex.cinema.api.dto.PagedResponse;
import com.lex.cinema.api.dto.ReservationCreateRequest;
import com.lex.cinema.model.Reservation;
import com.lex.cinema.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api2/reservations")
public class ReservationApiController {

    private final BookingService bookingService;

    public ReservationApiController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public PagedResponse<Reservation> list(
            @RequestParam(required = false) Long sessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        page = Math.max(page, 0);
        size = Math.min(Math.max(size, 1), 200);

        List<Reservation> items = bookingService.list(sessionId, page, size);
        long total = bookingService.count(sessionId);
        return new PagedResponse<>(items, page, size, total);
    }

    @GetMapping("/{id}")
    public Reservation get(@PathVariable long id) {
        return bookingService.get(id);
    }

    @PostMapping
    public ResponseEntity<Reservation> create(@RequestBody ReservationCreateRequest req) {
        Reservation created = bookingService.book(
                req.getSessionId(),
                req.getCustomerName(),
                req.getSeatIds()
        );
        return ResponseEntity.created(URI.create("/api2/reservations/" + created.getId())).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable long id) {
        bookingService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}
