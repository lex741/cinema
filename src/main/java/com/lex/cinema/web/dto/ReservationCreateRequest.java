package com.lex.cinema.web.dto;

import java.util.List;

public class ReservationCreateRequest {
    public long sessionId;
    public String customerName;
    public List<Long> seatIds;
}
