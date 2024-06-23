package com.example.task_manager.model;

public class User {
    private String name;
    private String dob;
    private String email;

    public User() {
        // Default constructor required for Firestore serialization
    }

    public User(String name, String dob, String email) {
        this.name = name;
        this.dob = dob;
        this.email = email;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
