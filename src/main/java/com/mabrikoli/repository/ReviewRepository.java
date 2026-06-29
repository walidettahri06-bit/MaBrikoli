package com.mabrikoli.repository;

import com.mabrikoli.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data access for {@link Review} entities.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Finds all reviews left for a specific artisan profile.
     */
    List<Review> findByArtisanId(Long artisanProfileId);

    /**
     * Finds the review associated with a specific booking.
     */
    Optional<Review> findByBookingId(Long bookingId);

    /**
     * Checks if a review exists for the given booking ID.
     */
    boolean existsByBookingId(Long bookingId);
}
