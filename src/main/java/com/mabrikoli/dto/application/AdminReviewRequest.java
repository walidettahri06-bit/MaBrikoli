package com.mabrikoli.dto.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for admin review (specifically for providing rejection reasons).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminReviewRequest {

    @NotBlank(message = "Notes/reason must not be blank")
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String reason;
}
