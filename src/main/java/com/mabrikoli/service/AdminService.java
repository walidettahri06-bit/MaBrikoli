package com.mabrikoli.service;

import com.mabrikoli.dto.admin.AdminStatsResponse;
import com.mabrikoli.enums.ApplicationStatus;
import com.mabrikoli.enums.Role;
import com.mabrikoli.repository.ArtisanApplicationRepository;
import com.mabrikoli.repository.ArtisanProfileRepository;
import com.mabrikoli.repository.BookingRepository;
import com.mabrikoli.repository.ReviewRepository;
import com.mabrikoli.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service managing administrative dashboard statistics.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ArtisanApplicationRepository artisanApplicationRepository;
    private final ArtisanProfileRepository artisanProfileRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;

    /**
     * Gathers platform-wide statistics for the administrator dashboard.
     */
    @Transactional(readOnly = true)
    public AdminStatsResponse getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalArtisans = userRepository.countByRole(Role.ROLE_ARTISAN);
        long pendingApplications = artisanApplicationRepository.countByStatus(ApplicationStatus.PENDING);
        long approvedArtisans = artisanProfileRepository.countByVerified(true);
        long totalBookings = bookingRepository.count();
        long totalReviews = reviewRepository.count();

        log.debug("Fetched dashboard statistics: users={}, artisans={}, pendingApps={}, approvedArtisans={}, bookings={}, reviews={}",
                totalUsers, totalArtisans, pendingApplications, approvedArtisans, totalBookings, totalReviews);

        return AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalArtisans(totalArtisans)
                .pendingApplications(pendingApplications)
                .approvedArtisans(approvedArtisans)
                .totalBookings(totalBookings)
                .totalReviews(totalReviews)
                .build();
    }
}
