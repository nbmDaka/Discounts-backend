package com.discount_backend.Discount_backend.service.user;

import com.discount_backend.Discount_backend.dto.UserProfileDto;
import com.discount_backend.Discount_backend.entity.user.User;
import com.discount_backend.Discount_backend.entity.user.UserProfile;
import com.discount_backend.Discount_backend.repository.user.UserProfileRepository;
import com.discount_backend.Discount_backend.repository.user.UserRepository;
import jakarta.transaction.Transactional;
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


    @Transactional
    public UserProfileDto updateMyProfile(String username, UserProfileDto dto) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserProfile profile = Optional.ofNullable(user.getProfile())
                .orElseGet(() -> {
                    UserProfile p = new UserProfile();
                    p.setUser(user);
                    return p;
                });

        // **Only overwrite when the DTO property is non-null**
        if (dto.getFirstName() != null) {
            profile.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            profile.setLastName(dto.getLastName());
        }
        if (dto.getEmail() != null) {
            profile.setEmail(dto.getEmail());
        }
        if (dto.getPhoneNumber() != null) {
            profile.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getAvatarUrl() != null) {
            profile.setAvatarUrl(dto.getAvatarUrl());
        }

        // persist (cascade to user if needed)
        profileRepo.save(profile);

        return toDto(profile);
    }

    private UserProfileDto toDto(UserProfile p) {
        UserProfileDto out = new UserProfileDto();
        out.setFirstName(p.getFirstName());
        out.setLastName(p.getLastName());
        out.setEmail(p.getEmail());
        out.setPhoneNumber(p.getPhoneNumber());
        out.setAvatarUrl(p.getAvatarUrl());
        return out;
    }
}
