package com.discount_backend.Discount_backend.controller;

import com.discount_backend.Discount_backend.dto.UserProfileDto;
import com.discount_backend.Discount_backend.service.UserProfileService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me")
public class UserProfileController {
    private final UserProfileService service;

    public UserProfileController(UserProfileService service) {
        this.service = service;
    }

    @GetMapping
    public UserProfileDto getProfile(@AuthenticationPrincipal UserDetails user) {
        return service.getMyProfile(user.getUsername());
    }

    @PatchMapping
    public UserProfileDto updateProfile(
            @AuthenticationPrincipal UserDetails user,
            @Validated @RequestBody UserProfileDto dto
    ) {
        return service.updateMyProfile(user.getUsername(), dto);
    }
}
