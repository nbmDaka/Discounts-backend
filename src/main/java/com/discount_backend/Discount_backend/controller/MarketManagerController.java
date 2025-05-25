package com.discount_backend.Discount_backend.controller;


import com.discount_backend.Discount_backend.dto.UserProfile.UserProfileDto;
import com.discount_backend.Discount_backend.dto.market.AssignManagerRequest;
import com.discount_backend.Discount_backend.service.MarketManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class MarketManagerController {

    private final MarketManagerService service;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserProfileDto>> getUsers() {
        return ResponseEntity.ok(service.getAllUsers());
    }

    @PostMapping("/markets/{marketId}/managers/{userId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> assignManager(
            @PathVariable Long marketId,
            @PathVariable Long userId
    ) {
        service.assignManager(marketId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/markets/{marketId}/managers/{userId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> removeManager(@PathVariable Long marketId, @PathVariable Long userId) {
        service.removeManager(marketId, userId);
        return ResponseEntity.noContent().build();
    }
} // End
