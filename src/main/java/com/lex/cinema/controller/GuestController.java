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
        model.addAttribute("sessions", sessionService.list(null, null, null, null, 0, 1000));
        return "guest/sessions";
    }

    @GetMapping("/sessions/{id}")
    public String session(@PathVariable long id, Model model) {
        MovieSession s = sessionService.get(id);

        Map<Integer, List<Seat>> seatRows = s.getSeats().stream()
                .collect(Collectors.groupingBy(
                        Seat::getRow,
                        TreeMap::new,
                        Collectors.toList()
                ));

        for (List<Seat> row : seatRows.values()) {
            row.sort(Comparator.comparingInt(Seat::getNumber));
        }

        model.addAttribute("session", s);
        model.addAttribute("seatRows", seatRows);
        return "guest/session";
    }

    @PostMapping("/sessions/{id}/order")
    public String order(
            @PathVariable long id,
            @RequestParam(required = false) List<Long> seatIds,
            @RequestParam(required = false) String customerName
    ) {
        Reservation r = bookingService.book(id, customerName, seatIds);
        return "redirect:/order/" + r.getId();
    }

    @GetMapping("/order/{orderId}")
    public String orderPage(@PathVariable long orderId, Model model) {
        Reservation r = bookingService.get(orderId);
        MovieSession s = sessionService.get(r.getSessionId());

        int seatsCount = (r.getSeatIds() == null) ? 0 : r.getSeatIds().size();
        BigDecimal total = s.getPrice().multiply(BigDecimal.valueOf(seatsCount));

        model.addAttribute("order", r);
        model.addAttribute("session", s);
        model.addAttribute("total", total);
        return "guest/order";
    }
}
