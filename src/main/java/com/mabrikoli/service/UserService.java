package com.mabrikoli.service;

import com.mabrikoli.common.exception.BadRequestException;
import com.mabrikoli.common.exception.ResourceNotFoundException;
import com.mabrikoli.dto.user.ChangePasswordRequest;
import com.mabrikoli.dto.user.UpdateProfileRequest;
import com.mabrikoli.dto.user.UserResponse;
import com.mabrikoli.entity.User;
import com.mabrikoli.mapper.UserMapper;
import com.mabrikoli.repository.UserRepository;
import com.mabrikoli.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business logic for user profile management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // ── Get Current User ─────────────────────────────────────

    /**
     * Returns the profile of the currently authenticated user.
     *
     * @param principal the authenticated user's security principal
     * @return the user's profile DTO
     */
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(UserPrincipal principal) {
        User user = findUserById(principal.getId());
        return userMapper.toResponse(user);
    }

    // ── Get User by ID ───────────────────────────────────────

    /**
     * Returns a user's public profile by ID.
     *
     * @param userId the user ID
     * @return the user's profile DTO
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = findUserById(userId);
        return userMapper.toResponse(user);
    }

    // ── Update Profile ───────────────────────────────────────

    /**
     * Updates the authenticated user's profile.
     * <p>
     * Only non-null fields in the request are applied.
     * Email and role cannot be changed through this method.
     *
     * @param principal the authenticated user
     * @param request   the fields to update
     * @return the updated profile
     */
    @Transactional
    public UserResponse updateProfile(UserPrincipal principal, UpdateProfileRequest request) {
        User user = findUserById(principal.getId());

        // Apply non-null fields
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            // Check phone uniqueness if changing
            if (!request.getPhoneNumber().equals(user.getPhoneNumber())
                    && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new BadRequestException("Phone number is already registered: " + request.getPhoneNumber());
            }
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }

        user = userRepository.save(user);
        log.info("Profile updated for user id: {}", user.getId());

        return userMapper.toResponse(user);
    }

    // ── Change Password ──────────────────────────────────────

    /**
     * Changes the authenticated user's password.
     *
     * @param principal the authenticated user
     * @param request   current password, new password, confirmation
     */
    @Transactional
    public void changePassword(UserPrincipal principal, ChangePasswordRequest request) {
        User user = findUserById(principal.getId());

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        // Verify new password matches confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("New password and confirmation do not match");
        }

        // Verify new password differs from current
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("New password must be different from the current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed for user id: {}", user.getId());
    }

    // ── Delete User ──────────────────────────────────────────

    /**
     * Soft-deletes the authenticated user's account by disabling it.
     *
     * @param principal the authenticated user
     */
    @Transactional
    public void deleteAccount(UserPrincipal principal) {
        User user = findUserById(principal.getId());
        user.setEnabled(false);
        userRepository.save(user);

        log.info("Account disabled (soft-deleted) for user id: {}", user.getId());
    }

    // ── Internal Helpers ─────────────────────────────────────

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
}
