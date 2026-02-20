package com.example.s_book;


import com.google.gson.annotations.SerializedName;

public class User {
    private Long id;
    private String name;
    private String email;
    private String password;

    // No-argument constructor (Required for Retrofit/Gson)
    public User() {}

    // Constructor for creating a login request
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}