package com.discount_backend.Discount_backend.service.user;

import com.discount_backend.Discount_backend.dto.UserProfileDto;
import com.discount_backend.Discount_backend.entity.user.User;
import com.discount_backend.Discount_backend.entity.user.UserProfile;
import com.discount_backend.Discount_backend.repository.user.UserProfileRepository;
import com.discount_backend.Discount_backend.repository.user.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserProfileService {
    private final UserRepository userRepo;
    private final UserProfileRepository profileRepo;

    public UserProfileService(UserRepository userRepo, UserProfileRepository profileRepo) {
        this.userRepo = userRepo;
        this.profileRepo = profileRepo;
    }

    public UserProfileDto getMyProfile(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UserProfile profile =
                Optional.ofNullable(user.getProfile()).orElseGet(() -> {
                    UserProfile p = new UserProfile();
                    p.setUser(user);
                    return p;
                });
        return toDto(profile);
    }

    public UserProfileDto updateMyProfile(String username, UserProfileDto dto) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UserProfile profile = Optional.ofNullable(user.getProfile())
                .orElseGet(() -> {
                    UserProfile p = new UserProfile();
                    p.setUser(user);
                    return p;
                });
        // apply changes from dto
        profile.setFirstName(dto.getFirstName());
        profile.setLastName(dto.getLastName());
        profile.setEmail(dto.getEmail());
        profile.setPhoneNumber(dto.getPhoneNumber());
        profile.setAvatarUrl(dto.getAvatarUrl());
        profileRepo.save(profile);
        return toDto(profile);
    }

    private UserProfileDto toDto(UserProfile p) {
        UserProfileDto d = new UserProfileDto();
        d.setFirstName(p.getFirstName());
        d.setLastName(p.getLastName());
        d.setEmail(p.getEmail());
        d.setPhoneNumber(p.getPhoneNumber());
        d.setAvatarUrl(p.getAvatarUrl());
        return d;
    }
}
