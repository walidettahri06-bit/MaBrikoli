package com.mabrikoli.controller;

import com.mabrikoli.common.dto.ApiResponse;
import com.mabrikoli.dto.user.ChangePasswordRequest;
import com.mabrikoli.dto.user.UpdateProfileRequest;
import com.mabrikoli.dto.user.UserResponse;
import com.mabrikoli.security.UserPrincipal;
import com.mabrikoli.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User profile management endpoints.
 * <p>
 * All endpoints require authentication (JWT token).
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile management")
public class UserController {

    private final UserService userService;

    // ── Get Current User ─────────────────────────────────────

    @Operation(summary = "Get the current authenticated user's profile")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal principal) {

        UserResponse response = userService.getCurrentUser(principal);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ── Get User by ID ───────────────────────────────────────

    @Operation(summary = "Get a user's profile by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable Long id) {

        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ── Update Profile ───────────────────────────────────────

    @Operation(summary = "Update the current user's profile")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request) {

        UserResponse response = userService.updateProfile(principal, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }

    // ── Change Password ──────────────────────────────────────

    @Operation(summary = "Change the current user's password")
    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(principal, request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

    // ── Delete Account ───────────────────────────────────────

    @Operation(summary = "Delete (disable) the current user's account")
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @AuthenticationPrincipal UserPrincipal principal) {

        userService.deleteAccount(principal);
        return ResponseEntity.ok(ApiResponse.success("Account deleted successfully"));
    }
}
