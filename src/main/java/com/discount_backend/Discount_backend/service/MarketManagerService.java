package com.discount_backend.Discount_backend.service;


import com.discount_backend.Discount_backend.dto.UserProfile.UserProfileDto;
import com.discount_backend.Discount_backend.dto.market.AssignManagerRequest;
import com.discount_backend.Discount_backend.entity.MarketManagers;
import com.discount_backend.Discount_backend.entity.Market;
import com.discount_backend.Discount_backend.entity.role.Role;
import com.discount_backend.Discount_backend.entity.user.User;
import com.discount_backend.Discount_backend.entity.user.UserProfile;
import com.discount_backend.Discount_backend.entity.user.UserRole;
import com.discount_backend.Discount_backend.repository.marketRepository.MarketManagerRepository;
import com.discount_backend.Discount_backend.repository.marketRepository.MarketRepository;
import com.discount_backend.Discount_backend.repository.role.RoleRepository;
import com.discount_backend.Discount_backend.repository.user.UserRepository;
import com.discount_backend.Discount_backend.repository.user.UserRoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketManagerService {

    private final MarketRepository marketRepository;
    private final UserRepository userRepository;
    private final MarketManagerRepository marketManagerRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public List<UserProfileDto> getAllUsers() {
        return userRepository.findAllWithRolesAndProfile()
                .stream()
                .map(user -> {
                    UserProfile profile = user.getProfile();
                    List<String> roles = user.getUserRoles().stream()
                            .map(userRole -> userRole.getRole().getName())
                            .toList();

                    return new UserProfileDto(
                            profile.getFirstName(),
                            profile.getLastName(),
                            profile.getEmail(),
                            profile.getPhoneNumber(),
                            profile.getAvatarUrl(),
                            roles

                    );
                })
                .toList();
    }

    @Transactional
    public void assignManager(Long marketId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (marketManagerRepository.findByUserId(user.getId()).isPresent()) {
            throw new RuntimeException("User already manages a market");
        }

        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new RuntimeException("Market not found"));

        MarketManagers managerEntry = new MarketManagers();
        managerEntry.setUser(user);
        managerEntry.setMarket(market);
        marketManagerRepository.save(managerEntry);

        // Fetch MANAGER role from DB
        Role managerRole = roleRepository.findByName("manager")
                .orElseThrow(() -> new RuntimeException("MANAGER role not found"));

        if (!userRoleRepository.existsByUserIdAndRole(user.getId(), managerRole)) {
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(managerRole);
            userRoleRepository.save(userRole);
        }
    }

    @Transactional
    public void removeManager(Long marketId, Long userId) {
        marketManagerRepository.deleteByMarketIdAndUserId(marketId, userId);

        // Fetch MANAGER role from DB
        Role managerRole = roleRepository.findByName("manager")
                .orElseThrow(() -> new RuntimeException("MANAGER role not found"));

        // Only remove role if user is not managing any other market
        if (marketManagerRepository.findByUserId(userId).isEmpty()) {
            userRoleRepository.deleteByUserIdAndRoleId(userId, managerRole.getId());
        }
    }

}
