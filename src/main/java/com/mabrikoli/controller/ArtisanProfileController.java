package com.mabrikoli.controller;

import com.mabrikoli.common.dto.ApiResponse;
import com.mabrikoli.dto.profile.ArtisanProfileRequest;
import com.mabrikoli.dto.profile.ArtisanProfileResponse;
import com.mabrikoli.security.UserPrincipal;
import com.mabrikoli.service.ArtisanProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller exposing artisan profile management endpoints.
 */
@RestController
@RequestMapping("/artisan-profiles")
@RequiredArgsConstructor
@Tag(name = "Artisan Profiles", description = "Browse and manage artisan profiles")
public class ArtisanProfileController {

    private final ArtisanProfileService artisanProfileService;

    // ── Public / Browsing Endpoints ──────────────────────────

    @Operation(summary = "Get all artisan profiles")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ArtisanProfileResponse>>> getAllProfiles() {
        List<ArtisanProfileResponse> responses = artisanProfileService.getAllProfiles();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @Operation(summary = "Get an artisan profile by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArtisanProfileResponse>> getProfileById(
            @PathVariable Long id) {
        ArtisanProfileResponse response = artisanProfileService.getProfileById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ── Self Management (Artisan Only) ───────────────────────

    @Operation(summary = "Get currently authenticated artisan's profile")
    @GetMapping("/me")
    @PreAuthorize("hasRole('ARTISAN')")
    public ResponseEntity<ApiResponse<ArtisanProfileResponse>> getMyProfile(
            @AuthenticationPrincipal UserPrincipal principal) {
        ArtisanProfileResponse response = artisanProfileService.getMyProfile(principal);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Update currently authenticated artisan's profile")
    @PutMapping("/me")
    @PreAuthorize("hasRole('ARTISAN')")
    public ResponseEntity<ApiResponse<ArtisanProfileResponse>> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ArtisanProfileRequest request) {
        ArtisanProfileResponse response = artisanProfileService.updateProfile(principal, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }

    // ── Admin Endpoints ──────────────────────────────────────

    @Operation(summary = "Delete an artisan profile (Admin only)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProfile(
            @PathVariable Long id) {
        artisanProfileService.deleteProfile(id);
        return ResponseEntity.ok(ApiResponse.success("Profile deleted successfully"));
    }
}
