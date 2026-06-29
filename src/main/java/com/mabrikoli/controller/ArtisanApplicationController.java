package com.mabrikoli.controller;

import com.mabrikoli.common.dto.ApiResponse;
import com.mabrikoli.dto.application.AdminReviewRequest;
import com.mabrikoli.dto.application.ArtisanApplicationRequest;
import com.mabrikoli.dto.application.ArtisanApplicationResponse;
import com.mabrikoli.security.UserPrincipal;
import com.mabrikoli.service.ArtisanApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for Artisan Applications.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Artisan Applications", description = "Artisan application submission and review endpoints")
public class ArtisanApplicationController {

    private final ArtisanApplicationService applicationService;

    // ── User / Applicant Endpoints ───────────────────────────

    @Operation(summary = "Submit a new artisan application")
    @PostMapping("/artisan-applications")
    public ResponseEntity<ApiResponse<ArtisanApplicationResponse>> submitApplication(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ArtisanApplicationRequest request) {

        ArtisanApplicationResponse response = applicationService.submitApplication(principal, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Artisan application submitted successfully", response));
    }

    @Operation(summary = "Get current user's artisan applications")
    @GetMapping("/artisan-applications/me")
    public ResponseEntity<ApiResponse<List<ArtisanApplicationResponse>>> getMyApplications(
            @AuthenticationPrincipal UserPrincipal principal) {

        List<ArtisanApplicationResponse> responses = applicationService.getMyApplications(principal);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    // ── Admin Endpoints ──────────────────────────────────────

    @Operation(summary = "List all artisan applications (Admin only)")
    @GetMapping("/admin/artisan-applications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ArtisanApplicationResponse>>> getAllApplications() {
        List<ArtisanApplicationResponse> responses = applicationService.getAllApplications();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @Operation(summary = "Get detailed view of a single application (Admin only)")
    @GetMapping("/admin/artisan-applications/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ArtisanApplicationResponse>> getApplicationById(
            @PathVariable Long id) {
        ArtisanApplicationResponse response = applicationService.getApplicationById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Approve an artisan application (Admin only)")
    @PutMapping("/admin/artisan-applications/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ArtisanApplicationResponse>> approveApplication(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal adminPrincipal) {

        ArtisanApplicationResponse response = applicationService.approveApplication(id, adminPrincipal);
        return ResponseEntity.ok(ApiResponse.success("Application approved successfully", response));
    }

    @Operation(summary = "Reject an artisan application (Admin only)")
    @PutMapping("/admin/artisan-applications/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ArtisanApplicationResponse>> rejectApplication(
            @PathVariable Long id,
            @Valid @RequestBody AdminReviewRequest reviewRequest,
            @AuthenticationPrincipal UserPrincipal adminPrincipal) {

        ArtisanApplicationResponse response = applicationService.rejectApplication(id, reviewRequest, adminPrincipal);
        return ResponseEntity.ok(ApiResponse.success("Application rejected successfully", response));
    }
}
