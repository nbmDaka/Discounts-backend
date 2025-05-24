package com.discount_backend.Discount_backend.dto.UserProfile;

import com.discount_backend.Discount_backend.entity.user.UserProfile;

import java.util.List;

public class UserProfileMapper {
    private UserProfileDto toDto(UserProfile profile) {
        UserProfileDto dto = new UserProfileDto();
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setEmail(profile.getEmail());
        dto.setPhoneNumber(profile.getPhoneNumber());
        dto.setAvatarUrl(profile.getAvatarUrl());

        // Add roles from user.userRoles -> role.name
        List<String> roleNames = profile.getUser().getUserRoles().stream()
                .map(userRole -> userRole.getRole().getName())
                .toList();
        dto.setRoles(roleNames);

        return dto;
    }

}
