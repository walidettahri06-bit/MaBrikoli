package com.mabrikoli.repository;

import com.mabrikoli.entity.ArtisanProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Data access for {@link ArtisanProfile} entities.
 */
@Repository
public interface ArtisanProfileRepository extends JpaRepository<ArtisanProfile, Long> {

    /**
     * Finds an artisan profile by the associated user ID.
     */
    Optional<ArtisanProfile> findByUserId(Long userId);

    /**
     * Counts artisan profiles by verification status.
     */
    long countByVerified(boolean verified);
}
