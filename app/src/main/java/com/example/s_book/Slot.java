package com.example.s_book;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class Slot {
    private Long id;
    private String startTime; // Comes as String in JSON
    private boolean booked;
    private LocalDateTime endTime;
    @SerializedName("bookedByName")
    private String bookedByName;
    public Long getId() { return id; }
    public String getStartTime() { return startTime; }
    public boolean isBooked() { return booked; }
    public String getBookedByName(){return bookedByName;}
    public void setBookedByName(String bookedByName){
        this.bookedByName=bookedByName;
    }


    public void setBooked(boolean b) {
        this.booked=b;
    }
}