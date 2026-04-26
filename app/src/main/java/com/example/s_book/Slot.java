package com.example.s_book;

import com.google.gson.annotations.SerializedName;

public class Slot {
    private Long id;
    private String startTime;
    private boolean booked;

    // FIX 1: Tell Gson that "vendor" in JSON maps to "vendorName" in Java
    @SerializedName("vendor")
    private Vendor vendorName;

    // FIX 2: Change to String to avoid LocalDateTime parsing errors in Review 2
    private String endTime;

    @SerializedName("bookedByName")
    private String bookedByName;

    // --- Getters ---
    public Long getId() { return id; }
    public String getStartTime() { return startTime; }
    public boolean isBooked() { return booked; }

    // FIX 3: Ensure this returns the String endTime
    public String getEndTime() { return endTime; }

    public Vendor getVendor() { return vendorName; }

    public String getBookedByName() { return bookedByName; }
    public void setBookedByName(String bookedByName) {
        this.bookedByName = bookedByName;
    }

    public void setBooked(boolean b) {
        this.booked = b;
    }
}