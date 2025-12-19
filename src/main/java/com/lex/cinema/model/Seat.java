package com.lex.cinema.model;

public class Seat {
    private Long id;
    private Long sessionId;
    private int row;
    private int number;
    private boolean reserved;

    public Seat() {}

    public Seat(Long id, Long sessionId, int row, int number, boolean reserved) {
        this.id = id;
        this.sessionId = sessionId;
        this.row = row;
        this.number = number;
        this.reserved = reserved;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public boolean isReserved() { return reserved; }
    public void setReserved(boolean reserved) { this.reserved = reserved; }
}
