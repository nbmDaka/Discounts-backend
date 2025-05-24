package com.discount_backend.Discount_backend.dto.UserProfile;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UserProfileDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private List<String> roles;


}
