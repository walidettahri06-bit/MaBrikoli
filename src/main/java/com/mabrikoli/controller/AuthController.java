package com.mabrikoli.controller;

import com.mabrikoli.common.dto.ApiResponse;
import com.mabrikoli.dto.auth.AuthResponse;
import com.mabrikoli.dto.auth.LoginRequest;
import com.mabrikoli.dto.auth.RegisterRequest;
import com.mabrikoli.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public authentication endpoints.
 * <p>
 * All routes under {@code /auth/**} are permitted without a token
 * (configured in {@link com.mabrikoli.config.SecurityConfig}).
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register and login endpoints")
public class AuthController {

    private final AuthService authService;

    // ── Registration ─────────────────────────────────────────

    @Operation(summary = "Register a new client account")
    @PostMapping("/register/client")
    public ResponseEntity<ApiResponse<AuthResponse>> registerClient(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.registerClient(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Client registered successfully", response));
    }

    @Operation(summary = "Register a new artisan account")
    @PostMapping("/register/artisan")
    public ResponseEntity<ApiResponse<AuthResponse>> registerArtisan(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.registerArtisan(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Artisan registered successfully", response));
    }

    // ── Login ────────────────────────────────────────────────

    @Operation(summary = "Authenticate and obtain JWT tokens")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);
        return ResponseEntity
                .ok(ApiResponse.success("Login successful", response));
    }
}
