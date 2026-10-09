package com.example.demo.service;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("An account already exists for this email.");
        }
        user.setRole(normalizePublicRole(user.getRole()));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User requireByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }

    public static Role normalizePublicRole(Role requested) {
        if (requested == Role.CREATOR) {
            return Role.CREATOR;
        }
        return Role.PARTICIPANT;
    }

    public static Role parseRole(String raw) {
        if (raw == null || raw.isBlank()) {
            return Role.PARTICIPANT;
        }
        String normalized = raw.replace("ROLE_", "").trim().toUpperCase(Locale.ROOT);
        if ("USER".equals(normalized)) {
            return Role.PARTICIPANT;
        }
        try {
            return Role.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            return Role.PARTICIPANT;
        }
    }
}
