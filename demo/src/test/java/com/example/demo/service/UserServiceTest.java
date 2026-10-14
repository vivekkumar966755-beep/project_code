package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should encode password before persisting registered user")
    void testRegisterUserPasswordEncoding() {
        User rawUser = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("plainTextPass")
                .role(Role.PARTICIPANT)
                .build();

        when(passwordEncoder.encode("plainTextPass")).thenReturn("$2a$10$hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User registered = userService.registerUser(rawUser);

        assertNotNull(registered);
        assertEquals("$2a$10$hashedPassword", registered.getPassword());
        verify(passwordEncoder, times(1)).encode("plainTextPass");
        verify(userRepository, times(1)).save(rawUser);
    }

    @Test
    @DisplayName("Should find user by email")
    void testFindByEmail() {
        User user = User.builder().email("found@example.com").build();
        when(userRepository.findByEmail("found@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("found@example.com");

        assertTrue(result.isPresent());
        assertEquals("found@example.com", result.get().getEmail());
    }
}
