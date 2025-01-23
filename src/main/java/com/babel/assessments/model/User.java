package com.babel.assessments.model;


import java.util.UUID;


public class User {
    public User() {
    }

    public User(UUID id, String name, String whatsapp, String email, String role) {
        this.id = id;
        this.name = name;
        this.whatsapp = whatsapp;
        this.email = email;
        this.role = role;
    }

    private UUID id;
    private String name;
    private String whatsapp;
    private String email;
    private String role;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


}
