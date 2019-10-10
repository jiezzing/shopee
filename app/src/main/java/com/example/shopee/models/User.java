package com.example.shopee.models;

public class User {
    private String id;
    private String firstname;
    private String latname;
    private String email;
    private String password;
    private String type;
    private String status;

    public User() {
    }

    public User(String id, String firstname, String latname, String email, String password, String type, String status) {
        this.id = id;
        this.firstname = firstname;
        this.latname = latname;
        this.email = email;
        this.password = password;
        this.type = type;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLatname() {
        return latname;
    }

    public void setLatname(String latname) {
        this.latname = latname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
