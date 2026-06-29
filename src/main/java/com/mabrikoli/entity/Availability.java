package com.mabrikoli.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * A single time slot in an artisan's weekly schedule.
 * <p>
 * Example: Monday 08:00–17:00, Tuesday 09:00–13:00.
 * Clients can consult this before booking.
 * Lifecycle is bound to {@link ArtisanProfile} (cascade ALL + orphanRemoval).
 */
@Entity
@Table(
        name = "availabilities",
        indexes = {
                @Index(name = "idx_avail_artisan", columnList = "artisan_profile_id"),
                @Index(name = "idx_avail_day", columnList = "day_of_week")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "artisanProfile")
public class Availability extends BaseEntity {

    /**
     * The artisan this schedule slot belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artisan_profile_id", nullable = false)
    private ArtisanProfile artisanProfile;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 10)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Builder.Default
    @Column(nullable = false)
    private boolean available = true;
}
