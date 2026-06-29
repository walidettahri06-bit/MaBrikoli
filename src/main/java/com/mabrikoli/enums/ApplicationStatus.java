package com.mabrikoli.enums;

/**
 * Status lifecycle for artisan applications.
 * <ul>
 *   <li>{@code PENDING}  — submitted, awaiting admin review</li>
 *   <li>{@code APPROVED} — accepted by an admin → artisan profile is created</li>
 *   <li>{@code REJECTED} — declined by an admin</li>
 * </ul>
 */
public enum ApplicationStatus {

    PENDING,
    APPROVED,
    REJECTED
}
