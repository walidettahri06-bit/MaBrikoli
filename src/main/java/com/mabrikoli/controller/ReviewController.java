package com.mabrikoli.controller;

import com.mabrikoli.common.dto.ApiResponse;
import com.mabrikoli.dto.review.ReviewRequest;
import com.mabrikoli.dto.review.ReviewResponse;
import com.mabrikoli.dto.review.ReviewUpdateRequest;
import com.mabrikoli.security.UserPrincipal;
import com.mabrikoli.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller exposing review endpoints.
 */
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Submit and read artisan reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "Submit a new artisan review (Clients only)")
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ReviewRequest request) {

        ReviewResponse response = reviewService.createReview(principal, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review submitted successfully", response));
    }

    @Operation(summary = "Update an existing review (Review owner only)")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        ReviewResponse response = reviewService.updateReview(id, request, principal);
        return ResponseEntity.ok(ApiResponse.success("Review updated successfully", response));
    }

    @Operation(summary = "Delete an artisan review (Review owner or Admin only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        reviewService.deleteReview(id, principal);
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully"));
    }

    @Operation(summary = "Get a review by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReviewById(
            @PathVariable Long id) {
        ReviewResponse response = reviewService.getReviewById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get all reviews left for a specific artisan")
    @GetMapping("/artisan/{artisanId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsForArtisan(
            @PathVariable Long artisanId) {
        List<ReviewResponse> responses = reviewService.getReviewsForArtisan(artisanId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
