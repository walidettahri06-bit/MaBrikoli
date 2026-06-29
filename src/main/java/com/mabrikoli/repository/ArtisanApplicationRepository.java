package com.mabrikoli.repository;

import com.mabrikoli.entity.ArtisanApplication;
import com.mabrikoli.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data access for {@link ArtisanApplication} entities.
 */
@Repository
public interface ArtisanApplicationRepository extends JpaRepository<ArtisanApplication, Long> {

    /**
     * Finds all applications submitted by a user.
     */
    List<ArtisanApplication> findByUserId(Long userId);

    /**
     * Checks if a user has an application with the specified status.
     */
    boolean existsByUserIdAndStatus(Long userId, ApplicationStatus status);

    /**
     * Counts applications with a specific status.
     */
    long countByStatus(ApplicationStatus status);
}
