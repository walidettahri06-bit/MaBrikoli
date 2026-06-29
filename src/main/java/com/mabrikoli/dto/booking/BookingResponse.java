package com.mabrikoli.dto.booking;

import com.mabrikoli.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Response payload representing a Booking.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private Long id;
    private Long clientId;
    private String clientName;
    private Long artisanId;
    private String artisanName;
    private Long categoryId;
    private String categoryName;
    private String description;
    private String address;
    private String city;
    private LocalDate bookingDate;
    private LocalTime preferredTime;
    private BookingStatus status;
    private BigDecimal estimatedPrice;
    private BigDecimal finalPrice;
    private String clientNotes;
    private String artisanNotes;
    private String cancellationReason;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
