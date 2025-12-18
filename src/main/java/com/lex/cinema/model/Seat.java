package com.lex.cinema.model;

public class Seat {
    private String id;      // наприклад "R3-S7"
    private int row;
    private int number;
    private boolean reserved;

    public Seat() {}

    public Seat(String id, int row, int number, boolean reserved) {
        this.id = id;
        this.row = row;
        this.number = number;
        this.reserved = reserved;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public boolean isReserved() { return reserved; }
    public void setReserved(boolean reserved) { this.reserved = reserved; }
}
