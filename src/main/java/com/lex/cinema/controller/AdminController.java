package com.lex.cinema.controller;

import com.lex.cinema.model.MovieSession;
import com.lex.cinema.service.BookingService;
import com.lex.cinema.service.MovieSessionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final MovieSessionService sessionService; // constructor injection
    private final BookingService bookingService;

    public AdminController(MovieSessionService sessionService, BookingService bookingService) {
        this.sessionService = sessionService;
        this.bookingService = bookingService;
    }

    @GetMapping("/sessions")
    public String sessions(Model model) {
        model.addAttribute("sessions", sessionService.list());
        return "admin/sessions";
    }

    @GetMapping("/sessions/new")
    public String newSession(Model model) {
        MovieSession s = new MovieSession();
        s.setMovieTitle("New movie");
        s.setHall("Hall 1");
        s.setStartTime(LocalDateTime.now().plusDays(1).withSecond(0).withNano(0));
        s.setPrice(new BigDecimal("150"));
        model.addAttribute("session", s);
        model.addAttribute("rows", 5);
        model.addAttribute("seatsPerRow", 8);
        model.addAttribute("mode", "create");
        return "admin/session-form";
    }

    @PostMapping("/sessions")
    public String createSession(
            @RequestParam String movieTitle,
            @RequestParam String hall,
            @RequestParam String startTime, // ISO-local, з html datetime-local
            @RequestParam BigDecimal price,
            @RequestParam int rows,
            @RequestParam int seatsPerRow
    ) {
        MovieSession s = new MovieSession();
        s.setMovieTitle(movieTitle);
        s.setHall(hall);
        s.setStartTime(LocalDateTime.parse(startTime));
        s.setPrice(price);
        sessionService.create(s, rows, seatsPerRow);
        return "redirect:/admin/sessions";
    }

    @GetMapping("/sessions/{id}/edit")
    public String editSession(@PathVariable Long id, Model model) {
        model.addAttribute("session", sessionService.getOrThrow(id));
        model.addAttribute("mode", "edit");
        return "admin/session-form";
    }

    @PostMapping("/sessions/{id}")
    public String updateSession(
            @PathVariable Long id,
            @RequestParam String movieTitle,
            @RequestParam String hall,
            @RequestParam String startTime,
            @RequestParam BigDecimal price
    ) {
        MovieSession s = sessionService.getOrThrow(id);
        s.setMovieTitle(movieTitle);
        s.setHall(hall);
        s.setStartTime(LocalDateTime.parse(startTime));
        s.setPrice(price);
        sessionService.update(s);
        return "redirect:/admin/sessions";
    }

    @PostMapping("/sessions/{id}/delete")
    public String deleteSession(@PathVariable Long id) {
        sessionService.delete(id);
        return "redirect:/admin/sessions";
    }

    @GetMapping("/reservations")
    public String reservations(Model model) {
        model.addAttribute("reservations", bookingService.listAll());
        return "admin/reservations";
    }

    @PostMapping("/reservations/{id}/cancel")
    public String cancelReservation(@PathVariable Long id) {
        bookingService.cancel(id);
        return "redirect:/admin/reservations";
    }
}
