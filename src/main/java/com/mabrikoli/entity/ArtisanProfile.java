package com.mabrikoli.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Business profile for a verified artisan.
 * <p>
 * Created only after an {@link ArtisanApplication} is approved by an admin.
 * Holds marketplace-visible data: bio, location, skills, availability,
 * and aggregated rating metrics.
 */
@Entity
@Table(
        name = "artisan_profiles",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_artisan_profiles_user", columnNames = "user_id")
        },
        indexes = {
                @Index(name = "idx_artisan_profiles_city", columnList = "city"),
                @Index(name = "idx_artisan_profiles_available", columnList = "available"),
                @Index(name = "idx_artisan_profiles_verified", columnList = "verified"),
                @Index(name = "idx_artisan_profiles_rating", columnList = "average_rating")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "categories", "bookings", "reviews", "availabilities"})
public class ArtisanProfile extends BaseEntity {

    // ── Owner of the 1:1 with User ──────────────────────────

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // ── Profile Fields ───────────────────────────────────────

    @Column(length = 1000)
    private String bio;

    @Column(name = "years_of_experience")
    private int yearsOfExperience;

    @Column(length = 100)
    private String city;

    @Column(length = 255)
    private String address;

    private Double latitude;

    private Double longitude;

    @Builder.Default
    @Column(nullable = false)
    private boolean available = true;

    @Builder.Default
    @Column(nullable = false)
    private boolean verified = false;

    @Column(name = "hourly_price")
    private Double hourlyPrice;

    // ── Aggregated Rating Metrics ────────────────────────────

    @Builder.Default
    @Column(name = "average_rating", nullable = false)
    private double averageRating = 0.0;

    @Builder.Default
    @Column(name = "total_reviews", nullable = false)
    private int totalReviews = 0;

    // ── Relationships ────────────────────────────────────────

    /**
     * Many-to-Many (owning side): The trades this artisan can perform.
     * Uses a dedicated join table {@code artisan_categories}.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "artisan_categories",
            joinColumns = @JoinColumn(name = "artisan_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private Set<Category> categories = new HashSet<>();

    /**
     * One-to-Many: Bookings received by this artisan.
     */
    @OneToMany(mappedBy = "artisan", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    /**
     * One-to-Many: Reviews received for this artisan.
     */
    @OneToMany(mappedBy = "artisan", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    /**
     * One-to-Many: Weekly availability schedule.
     * Cascade ALL + orphanRemoval because schedule slots are owned by the profile.
     */
    @OneToMany(mappedBy = "artisanProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Availability> availabilities = new ArrayList<>();

    // ── Helper Methods ───────────────────────────────────────

    public void addCategory(Category category) {
        this.categories.add(category);
        category.getArtisanProfiles().add(this);
    }

    public void removeCategory(Category category) {
        this.categories.remove(category);
        category.getArtisanProfiles().remove(this);
    }
}
