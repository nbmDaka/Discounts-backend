package com.discount_backend.Discount_backend.dto.auth;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private List<String> roles;
    // constructors, getters & setters

    public AuthResponse(String accessToken, String refreshToken, List<String> roles) {
        this.roles = roles;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }
}
