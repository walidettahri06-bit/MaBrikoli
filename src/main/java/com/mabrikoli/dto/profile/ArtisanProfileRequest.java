package com.mabrikoli.dto.profile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * Request payload for updating the authenticated artisan's profile.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtisanProfileRequest {

    @NotBlank(message = "Bio / Description is required")
    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;

    @Min(value = 0, message = "Years of experience must be 0 or greater")
    private int yearsOfExperience;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @NotNull(message = "Hourly price is required")
    @Min(value = 0, message = "Hourly price must be 0 or greater")
    private Double hourlyPrice;

    @Builder.Default
    private boolean available = true;

    private Set<Long> categoryIds;

    private List<AvailabilityRequest> availabilities;
}
