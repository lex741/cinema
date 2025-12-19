package com.lex.cinema.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovieSessionCreateRequest {
    public String movieTitle;
    public String hall;
    public LocalDateTime startTime;
    public BigDecimal price;
    public int rows;
    public int seatsPerRow;
}
