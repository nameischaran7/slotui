package com.example.s_book;

public class Vendor {
    private Long id;
    private String name;
    private String category;
    private String location;
    private Double pricePerHour;
    private String email;
    private String password;
    private String role;

    // Getters
    public Long getId() {
        return id;
    }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getLocation() { return location; }
    public  Double getPricePerHour(){ return pricePerHour; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public void setName(String name) {this.name=name;
    }

    public void setRole(String role) {
        this.role=role;
    }

    public String getRole() {
        return role;
    }

    public void setCategory(String category) {
        this.category=category;
    }

    public void setLocation(String location) {
this.location=location;    }

    public void setPricePerHour(Double pricePerHour) {this.pricePerHour=pricePerHour;
    }
}