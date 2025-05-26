package com.discount_backend.Discount_backend.service.user;

import com.discount_backend.Discount_backend.dto.UserProfile.UserProfileDto;
import com.discount_backend.Discount_backend.entity.City;
import com.discount_backend.Discount_backend.entity.objectfiles.ObjectType;
import com.discount_backend.Discount_backend.entity.user.User;
import com.discount_backend.Discount_backend.entity.user.UserProfile;
import com.discount_backend.Discount_backend.repository.CityRepository;
import com.discount_backend.Discount_backend.repository.user.UserProfileRepository;
import com.discount_backend.Discount_backend.repository.user.UserRepository;
import com.discount_backend.Discount_backend.service.ImageService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserProfileService {
    private final UserRepository userRepo;
    private final UserProfileRepository profileRepo;
    private final ImageService imageService;
    private final CityRepository cityRepository;

    public UserProfileService(
            UserRepository userRepo,
            UserProfileRepository profileRepo,
            ImageService imageService,
            CityRepository cityRepository
    ) {
        this.userRepo     = userRepo;
        this.profileRepo  = profileRepo;
        this.imageService = imageService;
        this.cityRepository = cityRepository;
    }

    public UserProfileDto getMyProfile(String username) {
        User user = userRepo.findByUsernameWithProfileRolesAndCity(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserProfile profile = Optional.ofNullable(user.getProfile())
                .orElseGet(() -> {
                    UserProfile p = new UserProfile(user); // link user to new profile
                    return p;
                });

        UserProfileDto dto = toDto(profile);

        // Override avatar URL from image service
        String avatarUrl = imageService.getActiveImageUrl(ObjectType.USER, user.getId());
        dto.setAvatarUrl(avatarUrl);

        return dto;
    }


    @Transactional
    public UserProfileDto updateMyProfile(String username, UserProfileDto dto) {
        User user = userRepo.findByUsernameWithProfileAndRoles(username)
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

        if (dto.getCityId() != null) {
            City city = cityRepository.findById(dto.getCityId())
                    .orElseThrow(() -> new IllegalArgumentException("City not found"));
            profile.setCity(city);
        }

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

        List<String> roleNames = p.getUser()
                .getUserRoles()
                .stream()
                .map(userRole -> userRole.getRole().getName())
                .toList();
        out.setRoles(roleNames);

        if (p.getCity() != null) {
            out.setCityId(p.getCity().getId());
            out.setCityName(p.getCity().getName());
        }
        return out;
    }
}
