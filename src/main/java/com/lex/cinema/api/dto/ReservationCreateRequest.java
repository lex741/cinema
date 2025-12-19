package com.lex.cinema.api.dto;

import java.util.List;

public class ReservationCreateRequest {
    private long sessionId;
    private String customerName;
    private List<Long> seatIds;

    public long getSessionId() { return sessionId; }
    public void setSessionId(long sessionId) { this.sessionId = sessionId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public List<Long> getSeatIds() { return seatIds; }
    public void setSeatIds(List<Long> seatIds) { this.seatIds = seatIds; }
}
