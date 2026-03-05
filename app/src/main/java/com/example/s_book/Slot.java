package com.example.s_book;

import java.time.LocalDateTime;

public class Slot {
    private Long id;
    private String startTime; // Comes as String in JSON
    private boolean booked;
    private LocalDateTime endTime;
    private String bookedByName;
    public Long getId() { return id; }
    public String getStartTime() { return startTime; }
    public boolean isBooked() { return booked; }
    public String getBookedByName(){return bookedByName;}



}