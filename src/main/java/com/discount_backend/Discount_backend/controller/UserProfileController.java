package com.discount_backend.Discount_backend.controller;

import com.discount_backend.Discount_backend.dto.UserProfileDto;
import com.discount_backend.Discount_backend.entity.objectfiles.ObjectType;
import com.discount_backend.Discount_backend.service.ImageService;
import com.discount_backend.Discount_backend.service.user.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users/me")
public class UserProfileController {
    private final UserProfileService service;
    private final BaseUploadController uploadController;

    public UserProfileController(UserProfileService service, ImageService imageService) {
        this.service = service;
        this.uploadController = new BaseUploadController(imageService);
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

    @PostMapping("/{userId}/image")
    public ResponseEntity<String> uploadUserImage(@PathVariable Long userId,
                                                  @RequestParam MultipartFile file) {
        return uploadController.handleUpload(userId, ObjectType.USER, file);
    }

}
