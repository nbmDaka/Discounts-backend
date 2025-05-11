package com.discount_backend.Discount_backend.dto;


import java.util.List;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private List<String> roles;
    // constructors, getters & setters

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public AuthResponse(String accessToken, String refreshToken, List<String> roles) {
        this.roles = roles;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }
}
