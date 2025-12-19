package com.lex.cinema.api.dto;

import java.util.List;

public class ReservationCreateRequest {
    private Long sessionId;
    private String customerName;
    private List<String> seatIds;

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public List<String> getSeatIds() { return seatIds; }
    public void setSeatIds(List<String> seatIds) { this.seatIds = seatIds; }
}
