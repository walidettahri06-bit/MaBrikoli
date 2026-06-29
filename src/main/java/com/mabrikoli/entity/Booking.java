package com.mabrikoli.entity;

import com.mabrikoli.enums.BookingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * A service booking between a client and an artisan.
 * <p>
 * Lifecycle: {@code PENDING → CONFIRMED → IN_PROGRESS → COMPLETED}
 * (or {@code CANCELLED} at any point before completion).
 */
@Entity
@Table(
        name = "bookings",
        indexes = {
                @Index(name = "idx_bookings_client", columnList = "client_id"),
                @Index(name = "idx_bookings_artisan", columnList = "artisan_id"),
                @Index(name = "idx_bookings_status", columnList = "status"),
                @Index(name = "idx_bookings_date", columnList = "booking_date"),
                @Index(name = "idx_bookings_client_status", columnList = "client_id, status"),
                @Index(name = "idx_bookings_artisan_status", columnList = "artisan_id, status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"client", "artisan", "category", "review"})
public class Booking extends BaseEntity {

    // ── Participants ─────────────────────────────────────────

    /**
     * The client who requested the service.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    /**
     * The artisan performing the service.
     * Points to ArtisanProfile (not User) — only verified artisans can be booked.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artisan_id", nullable = false)
    private ArtisanProfile artisan;

    /**
     * The type of service requested.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // ── Booking Details ──────────────────────────────────────

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @Column(name = "preferred_time")
    private LocalTime preferredTime;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private BookingStatus status = BookingStatus.PENDING;

    // ── Pricing ──────────────────────────────────────────────

    @Column(name = "estimated_price", precision = 10, scale = 2)
    private BigDecimal estimatedPrice;

    @Column(name = "final_price", precision = 10, scale = 2)
    private BigDecimal finalPrice;

    // ── Notes ────────────────────────────────────────────────

    @Column(name = "client_notes", length = 1000)
    private String clientNotes;

    @Column(name = "artisan_notes", length = 1000)
    private String artisanNotes;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    // ── Timestamps ───────────────────────────────────────────

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    // ── Relationships ────────────────────────────────────────

    /**
     * One-to-One (inverse side): The review left after this booking is completed.
     * Mapped by {@link Review#booking}.
     */
    @OneToOne(mappedBy = "booking", fetch = FetchType.LAZY)
    private Review review;
}
