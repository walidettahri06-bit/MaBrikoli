package com.mabrikoli.repository;

import com.mabrikoli.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data access for {@link Booking} entities.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Finds all bookings requested by a client.
     */
    List<Booking> findByClientId(Long clientId);

    /**
     * Finds all bookings assigned to an artisan (by ArtisanProfile ID).
     */
    List<Booking> findByArtisanId(Long artisanProfileId);

    /**
     * Finds all bookings assigned to an artisan (by the Artisan's User ID).
     */
    @Query("SELECT b FROM Booking b WHERE b.artisan.user.id = :userId")
    List<Booking> findByArtisanUserId(@Param("userId") Long userId);
}
