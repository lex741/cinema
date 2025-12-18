package com.lex.cinema.controller;

import com.lex.cinema.model.MovieSession;
import com.lex.cinema.model.Reservation;
import com.lex.cinema.model.Seat;
import com.lex.cinema.service.BookingService;
import com.lex.cinema.service.MovieSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Controller
public class GuestController {

    private MovieSessionService sessionService;
    private BookingService bookingService;

    @Autowired
    public void setSessionService(MovieSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Autowired
    public void setBookingService(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/sessions";
    }

    @GetMapping("/sessions")
    public String sessions(Model model) {
        model.addAttribute("sessions", sessionService.list());
        return "guest/sessions";
    }

    @GetMapping("/sessions/{id}")
    public String session(@PathVariable Long id, Model model) {
        MovieSession s = sessionService.getOrThrow(id);

        // групуємо місця по рядах для красивої “карти”
        Map<Integer, List<Seat>> seatRows = s.getSeats().stream()
                .collect(Collectors.groupingBy(
                        Seat::getRow,
                        TreeMap::new,
                        Collectors.toList()
                ));
        seatRows.values().forEach(list -> list.sort(Comparator.comparingInt(Seat::getNumber)));

        model.addAttribute("session", s);
        model.addAttribute("sessionId", id);
        model.addAttribute("seatRows", seatRows);

        return "guest/session";
    }

    @PostMapping("/sessions/{id}/order")
    public String order(
            @PathVariable Long id,
            @RequestParam(required = false) List<String> seatIds,
            @RequestParam(required = false) String customerName
    ) {
        Reservation r = bookingService.book(id, customerName, seatIds);
        return "redirect:/order/" + r.getId();
    }

    @GetMapping("/order/{orderId}")
    public String orderPage(@PathVariable Long orderId, Model model) {
        Reservation r = bookingService.getOrThrow(orderId);
        MovieSession s = sessionService.getOrThrow(r.getSessionId());

        BigDecimal total = s.getPrice().multiply(BigDecimal.valueOf(r.getSeatIds().size()));
        model.addAttribute("order", r);
        model.addAttribute("session", s);
        model.addAttribute("total", total);
        return "guest/order";
    }
}
