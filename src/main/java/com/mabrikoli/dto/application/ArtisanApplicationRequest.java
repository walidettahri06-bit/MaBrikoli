package com.mabrikoli.dto.application;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for submitting an artisan application.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtisanApplicationRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Phone number must be valid (8-15 digits)")
    private String phoneNumber;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Min(value = 0, message = "Years of experience must be 0 or greater")
    private int yearsOfExperience;

    @NotBlank(message = "Description/motivation is required")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Diploma file URL is required")
    private String diplomaFileUrl;

    @NotBlank(message = "National ID file URL is required")
    private String nationalIdFileUrl;

    @NotBlank(message = "Personal photo URL is required")
    private String personalPhotoUrl;
}
