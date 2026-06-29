package com.mabrikoli.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A client's rating and comment for a completed booking.
 * <p>
 * Business rules:
 * <ul>
 *   <li>Exactly one review per booking (enforced by UNIQUE on {@code booking_id})</li>
 *   <li>Only bookings with status {@code COMPLETED} are eligible</li>
 *   <li>Rating is an integer from 1 to 5</li>
 * </ul>
 */
@Entity
@Table(
        name = "reviews",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_reviews_booking", columnNames = "booking_id")
        },
        indexes = {
                @Index(name = "idx_reviews_artisan", columnList = "artisan_id"),
                @Index(name = "idx_reviews_client", columnList = "client_id"),
                @Index(name = "idx_reviews_rating", columnList = "rating")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"booking", "client", "artisan"})
public class Review extends BaseEntity {

    // ── Owning side of the 1:1 with Booking ──────────────────

    /**
     * The booking this review is for. UNIQUE constraint ensures one review per booking.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    // ── Participants (denormalised for query efficiency) ──────

    /**
     * The client who wrote the review.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    /**
     * The artisan being reviewed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artisan_id", nullable = false)
    private ArtisanProfile artisan;

    // ── Review Content ───────────────────────────────────────

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private int rating;

    @Column(length = 2000)
    private String comment;
}
