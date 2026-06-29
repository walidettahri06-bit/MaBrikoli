package com.mabrikoli.enums;

/**
 * Status lifecycle for service bookings.
 * <ul>
 *   <li>{@code PENDING}     — client submitted, artisan has not responded</li>
 *   <li>{@code CONFIRMED}   — artisan accepted the booking</li>
 *   <li>{@code IN_PROGRESS} — work is underway</li>
 *   <li>{@code COMPLETED}   — work finished, eligible for review</li>
 *   <li>{@code CANCELLED}   — cancelled by either party</li>
 * </ul>
 */
public enum BookingStatus {

    PENDING,
    ACCEPTED,
    REJECTED,
    COMPLETED,
    CANCELLED
}
