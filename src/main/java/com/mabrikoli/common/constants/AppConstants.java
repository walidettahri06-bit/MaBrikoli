package com.mabrikoli.common.constants;

/**
 * Application-wide constants.
 */
public final class AppConstants {

    private AppConstants() {
        // Utility class — no instantiation
    }

    // ── Pagination ───────────────────────────────────────────
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE   = "10";
    public static final int MAX_PAGE_SIZE           = 50;

    // ── Roles ────────────────────────────────────────────────
    public static final String ROLE_ADMIN   = "ROLE_ADMIN";
    public static final String ROLE_CLIENT  = "ROLE_CLIENT";
    public static final String ROLE_ARTISAN = "ROLE_ARTISAN";

    // ── Validation Patterns ──────────────────────────────────
    public static final String PHONE_PATTERN = "^\\+?[0-9]{8,15}$";
    public static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

    // ── Messages ─────────────────────────────────────────────
    public static final String RESOURCE_NOT_FOUND = "%s not found with %s: '%s'";
    public static final String OPERATION_SUCCESSFUL = "Operation completed successfully";
}
