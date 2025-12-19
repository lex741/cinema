package com.lex.cinema.web;

import com.lex.cinema.model.Reservation;
import com.lex.cinema.service.BookingService;
import com.lex.cinema.web.dto.PagedResponse;
import com.lex.cinema.web.dto.ReservationCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservations", description = "Бронювання місць + транзакції")
public class ReservationsRestController {

    private final BookingService service;

    public ReservationsRestController(BookingService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Список бронювань", description = "Фільтр sessionId + пагінація page/size.")
    public PagedResponse<Reservation> list(
            @RequestParam(required = false) Long sessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var items = service.list(sessionId, page, size);
        var total = service.count(sessionId);
        return new PagedResponse<>(items, page, size, total);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Отримати бронювання", description = "Повертає бронювання з seatIds.")
    public Reservation get(@PathVariable long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Створити бронювання", description = "Транзакційно бронює seatIds для sessionId. Може повернути 409.")
    public Reservation create(@RequestBody ReservationCreateRequest req) {
        return service.book(req.sessionId, req.customerName, req.seatIds);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Скасувати бронювання", description = "Видаляє бронювання і звільняє місця (транзакційно).")
    public void cancel(@PathVariable long id) {
        service.cancel(id);
    }
}
