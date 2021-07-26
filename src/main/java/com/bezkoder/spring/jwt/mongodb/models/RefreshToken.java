package com.bezkoder.spring.jwt.mongodb.models;

import java.time.Instant;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class RefreshToken {

    @Id
    private String id;

    @NotBlank
    private User user;

    @NotBlank
    private String token;

    @NotBlank
    private Instant expiryDate;

    public RefreshToken() {
    }

    public RefreshToken(String id, @NotBlank User user, @NotBlank String token, @NotBlank Instant expiryDate) {
        this.id = id;
        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

}