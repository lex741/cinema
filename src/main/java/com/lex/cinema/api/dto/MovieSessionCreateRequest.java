package com.lex.cinema.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovieSessionCreateRequest {
    private String movieTitle;
    private String hall;
    private LocalDateTime startTime;
    private BigDecimal price;
    private Integer rows;
    private Integer seatsPerRow;

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getHall() { return hall; }
    public void setHall(String hall) { this.hall = hall; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getRows() { return rows; }
    public void setRows(Integer rows) { this.rows = rows; }

    public Integer getSeatsPerRow() { return seatsPerRow; }
    public void setSeatsPerRow(Integer seatsPerRow) { this.seatsPerRow = seatsPerRow; }
}
