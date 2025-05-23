package com.discount_backend.Discount_backend.service.user;

import com.discount_backend.Discount_backend.dto.UserProfileDto;
import com.discount_backend.Discount_backend.entity.objectfiles.ObjectType;
import com.discount_backend.Discount_backend.entity.user.User;
import com.discount_backend.Discount_backend.entity.user.UserProfile;
import com.discount_backend.Discount_backend.repository.user.UserProfileRepository;
import com.discount_backend.Discount_backend.repository.user.UserRepository;
import com.discount_backend.Discount_backend.service.ImageService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserProfileService {
    private final UserRepository userRepo;
    private final UserProfileRepository profileRepo;
    private final ImageService imageService;

    public UserProfileService(
            UserRepository userRepo,
            UserProfileRepository profileRepo,
            ImageService imageService
    ) {
        this.userRepo     = userRepo;
        this.profileRepo  = profileRepo;
        this.imageService = imageService;
    }

    public UserProfileDto getMyProfile(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserProfile profile = Optional.ofNullable(user.getProfile())
                .orElseGet(() -> {
                    UserProfile p = new UserProfile();
                    p.setUser(user);
                    return p;
                });

        UserProfileDto dto = toDto(profile);

        // **override** with the Cloudinary URL from object_file
        String avatarUrl = imageService.getActiveImageUrl(ObjectType.USER, user.getId());
        dto.setAvatarUrl(avatarUrl);

        return dto;
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

        // Only overwrite text fields
        if (dto.getFirstName() != null)    profile.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null)     profile.setLastName(dto.getLastName());
        if (dto.getEmail() != null)        profile.setEmail(dto.getEmail());
        if (dto.getPhoneNumber() != null)  profile.setPhoneNumber(dto.getPhoneNumber());
        // **don’t overwrite avatarUrl here** — it’s managed via ImageService/file upload

        profileRepo.save(profile);

        UserProfileDto out = toDto(profile);
        // again override with the real URL
        out.setAvatarUrl(
                imageService.getActiveImageUrl(ObjectType.USER, user.getId())
        );
        return out;
    }

    private UserProfileDto toDto(UserProfile p) {
        UserProfileDto out = new UserProfileDto();
        out.setFirstName(p.getFirstName());
        out.setLastName(p.getLastName());
        out.setEmail(p.getEmail());
        out.setPhoneNumber(p.getPhoneNumber());
        // out.setAvatarUrl(p.getAvatarUrl());  // removed
        return out;
    }
}
