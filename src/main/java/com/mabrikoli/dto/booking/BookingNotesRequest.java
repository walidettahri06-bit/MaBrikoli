package com.mabrikoli.dto.booking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for cancellation reasons or artisan rejection notes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingNotesRequest {

    @NotBlank(message = "Notes/reason must not be blank")
    @Size(max = 1000, message = "Reason/notes must not exceed 1000 characters")
    private String reason;
}
