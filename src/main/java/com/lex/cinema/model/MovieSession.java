package com.lex.cinema.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class MovieSession {
    private Long id;
    private String movieTitle;
    private String hall;
    private LocalDateTime startTime;
    private BigDecimal price;
    private List<Seat> seats;

    public MovieSession() {}

    public MovieSession(Long id, String movieTitle, String hall, LocalDateTime startTime, BigDecimal price) {
        this.id = id;
        this.movieTitle = movieTitle;
        this.hall = hall;
        this.startTime = startTime;
        this.price = price;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getHall() { return hall; }
    public void setHall(String hall) { this.hall = hall; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public List<Seat> getSeats() { return seats; }
    public void setSeats(List<Seat> seats) { this.seats = seats; }
}
