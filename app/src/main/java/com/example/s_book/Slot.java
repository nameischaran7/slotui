package com.example.s_book;

public class Slot {
    private Long id;
    private String startTime; // Comes as String in JSON
    private boolean booked;

    public Long getId() { return id; }
    public String getStartTime() { return startTime; }
    public boolean isBooked() { return booked; }
}