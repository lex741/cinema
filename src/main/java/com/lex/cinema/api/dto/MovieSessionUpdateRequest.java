package com.lex.cinema.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovieSessionUpdateRequest {
    private String movieTitle;
    private String hall;
    private LocalDateTime startTime;
    private BigDecimal price;

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getHall() { return hall; }
    public void setHall(String hall) { this.hall = hall; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
