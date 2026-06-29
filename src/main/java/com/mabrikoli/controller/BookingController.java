package com.mabrikoli.controller;

import com.mabrikoli.common.dto.ApiResponse;
import com.mabrikoli.dto.booking.BookingNotesRequest;
import com.mabrikoli.dto.booking.BookingRequest;
import com.mabrikoli.dto.booking.BookingResponse;
import com.mabrikoli.security.UserPrincipal;
import com.mabrikoli.service.BookingService;
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
 * REST controller for service Bookings.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Service booking management and status updates")
public class BookingController {

    private final BookingService bookingService;

    // ── Client Endpoints ─────────────────────────────────────

    @Operation(summary = "Submit a new service booking request")
    @PostMapping("/bookings")
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody BookingRequest request) {

        BookingResponse response = bookingService.createBooking(principal, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Booking request submitted successfully", response));
    }

    @Operation(summary = "Get bookings requested by currently logged-in client")
    @GetMapping("/bookings/client")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getClientBookings(
            @AuthenticationPrincipal UserPrincipal principal) {

        List<BookingResponse> responses = bookingService.getClientBookings(principal);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    // ── Artisan Endpoints ────────────────────────────────────

    @Operation(summary = "Get bookings received by currently logged-in artisan")
    @GetMapping("/bookings/artisan")
    @PreAuthorize("hasRole('ARTISAN')")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getArtisanBookings(
            @AuthenticationPrincipal UserPrincipal principal) {

        List<BookingResponse> responses = bookingService.getArtisanBookings(principal);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @Operation(summary = "Accept a booking request (Artisan only)")
    @PutMapping("/bookings/{id}/accept")
    @PreAuthorize("hasRole('ARTISAN')")
    public ResponseEntity<ApiResponse<BookingResponse>> acceptBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        BookingResponse response = bookingService.acceptBooking(id, principal);
        return ResponseEntity.ok(ApiResponse.success("Booking accepted successfully", response));
    }

    @Operation(summary = "Reject a booking request (Artisan only)")
    @PutMapping("/bookings/{id}/reject")
    @PreAuthorize("hasRole('ARTISAN')")
    public ResponseEntity<ApiResponse<BookingResponse>> rejectBooking(
            @PathVariable Long id,
            @Valid @RequestBody BookingNotesRequest notesRequest,
            @AuthenticationPrincipal UserPrincipal principal) {

        BookingResponse response = bookingService.rejectBooking(id, notesRequest, principal);
        return ResponseEntity.ok(ApiResponse.success("Booking rejected successfully", response));
    }

    @Operation(summary = "Complete an accepted booking request (Artisan only)")
    @PutMapping("/bookings/{id}/complete")
    @PreAuthorize("hasRole('ARTISAN')")
    public ResponseEntity<ApiResponse<BookingResponse>> completeBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        BookingResponse response = bookingService.completeBooking(id, principal);
        return ResponseEntity.ok(ApiResponse.success("Booking completed successfully", response));
    }

    // ── Mutual Participant Endpoints ─────────────────────────

    @Operation(summary = "Get a detailed view of a booking by ID")
    @GetMapping("/bookings/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        BookingResponse response = bookingService.getBookingById(id, principal);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Cancel a pending or accepted booking")
    @PutMapping("/bookings/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(
            @PathVariable Long id,
            @Valid @RequestBody BookingNotesRequest notesRequest,
            @AuthenticationPrincipal UserPrincipal principal) {

        BookingResponse response = bookingService.cancelBooking(id, notesRequest, principal);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully", response));
    }

    // ── Admin Endpoints ──────────────────────────────────────

    @Operation(summary = "List all bookings (Admin only)")
    @GetMapping("/admin/bookings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getAllBookings() {
        List<BookingResponse> responses = bookingService.getAllBookings();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
