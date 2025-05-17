package com.discount_backend.Discount_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserProfileDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;


}
