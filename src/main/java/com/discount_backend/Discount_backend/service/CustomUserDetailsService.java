package com.discount_backend.Discount_backend.service;


import com.discount_backend.Discount_backend.entity.User;
import com.discount_backend.Discount_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> authorities = user.getUserRoles().stream()
                .map(ur -> new SimpleGrantedAuthority(ur.getRole().getName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), user.isEnabled(), true, true, true, authorities);
    }
}