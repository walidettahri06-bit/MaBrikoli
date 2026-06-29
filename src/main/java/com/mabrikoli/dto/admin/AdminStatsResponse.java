package com.mabrikoli.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing platform dashboard statistics for administrators.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsResponse {

    private long totalUsers;
    private long totalArtisans;
    private long pendingApplications;
    private long approvedArtisans;
    private long totalBookings;
    private long totalReviews;
}
