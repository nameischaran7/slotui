package com.example.s_book;

public class Vendor {
    private Long id;
    private String name;
    private String category;
    private String location;
    private Double pricePerHour;

    // Getters
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getLocation() { return location; }
    public  Double getPricePerHour(){ return pricePerHour; }
}