package com.mabrikoli.dto.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating the current user's profile.
 * <p>
 * All fields are optional — only non-null values are applied.
 * Email and role changes are <strong>not</strong> permitted through this endpoint.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    private String firstName;

    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    private String lastName;

    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Phone number must be valid (8-15 digits)")
    private String phoneNumber;

    private String profileImageUrl;
}
