package com.lex.cinema.model;

import java.time.LocalDateTime;
import java.util.List;

public class Reservation {
    private Long id;
    private Long sessionId;
    private String customerName;
    private LocalDateTime createdAt;
    private List<Long> seatIds;

    public Reservation() {}

    public Reservation(Long id, Long sessionId, String customerName, LocalDateTime createdAt, List<Long> seatIds) {
        this.id = id;
        this.sessionId = sessionId;
        this.customerName = customerName;
        this.createdAt = createdAt;
        this.seatIds = seatIds;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<Long> getSeatIds() { return seatIds; }
    public void setSeatIds(List<Long> seatIds) { this.seatIds = seatIds; }
}
