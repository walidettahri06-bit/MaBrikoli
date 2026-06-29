package com.mabrikoli.dto.profile;

import com.mabrikoli.dto.category.CategoryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

/**
 * Response payload exposing detailed artisan profile details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtisanProfileResponse {

    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String profilePhoto;
    private String bio;
    private int yearsOfExperience;
    private String city;
    private String address;
    private Double hourlyPrice;
    private double averageRating;
    private int totalReviews;
    private boolean available;
    private boolean verified;
    private Set<CategoryResponse> categories;
    private List<AvailabilityInfo> availabilities;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailabilityInfo {
        private Long id;
        private DayOfWeek dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;
        private boolean available;
    }
}
