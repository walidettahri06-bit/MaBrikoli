package com.mabrikoli.service;

import com.mabrikoli.common.exception.BadRequestException;
import com.mabrikoli.dto.auth.AuthResponse;
import com.mabrikoli.dto.auth.LoginRequest;
import com.mabrikoli.dto.auth.RegisterRequest;
import com.mabrikoli.entity.User;
import com.mabrikoli.enums.Role;
import com.mabrikoli.repository.UserRepository;
import com.mabrikoli.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles user registration and authentication.
 *
 * <ul>
 *   <li>{@code registerClient}  — creates a {@code ROLE_CLIENT} user</li>
 *   <li>{@code registerArtisan} — creates a {@code ROLE_ARTISAN} user (can log in,
 *       but is <strong>not visible</strong> until an admin approves the application
 *       and an {@code ArtisanProfile} is created)</li>
 *   <li>{@code login}           — authenticates and issues JWT tokens</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    // ── Registration ─────────────────────────────────────────

    /**
     * Registers a new client user.
     */
    @Transactional
    public AuthResponse registerClient(RegisterRequest request) {
        return registerUser(request, Role.ROLE_CLIENT);
    }

    /**
     * Registers a new artisan user.
     * <p>
     * The user can log in immediately, but will not appear on the platform
     * until they submit an {@code ArtisanApplication} that an admin approves.
     */
    @Transactional
    public AuthResponse registerArtisan(RegisterRequest request) {
        return registerUser(request, Role.ROLE_ARTISAN);
    }

    // ── Login ────────────────────────────────────────────────

    /**
     * Authenticates a user and returns JWT tokens.
     */
    public AuthResponse login(LoginRequest request) {
        log.debug("Login attempt for email: {}", request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        log.info("User logged in successfully: {}", user.getEmail());

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    // ── Internal Helpers ─────────────────────────────────────

    private AuthResponse registerUser(RegisterRequest request, Role role) {
        log.debug("Registration attempt — email: {}, role: {}", request.getEmail(), role);

        // Uniqueness checks
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered: " + request.getEmail());
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()
                && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BadRequestException("Phone number is already registered: " + request.getPhoneNumber());
        }

        // Build and save user
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(role)
                .enabled(true)
                .emailVerified(false)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully — id: {}, email: {}, role: {}",
                user.getId(), user.getEmail(), role);

        // Auto-login after registration
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .build();
    }
}
