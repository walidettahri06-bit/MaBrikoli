package com.mabrikoli.common.validation;

import com.mabrikoli.common.constants.AppConstants;
import com.mabrikoli.common.exception.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Reusable validation and pagination helpers.
 */
public final class ValidationUtils {

    private ValidationUtils() {
        // Utility class — no instantiation
    }

    /**
     * Builds a validated {@link Pageable} from raw request parameters.
     *
     * @param page      zero-based page index
     * @param size      page size (clamped to {@link AppConstants#MAX_PAGE_SIZE})
     * @param sortBy    field to sort by
     * @param sortDir   {@code "asc"} or {@code "desc"}
     * @return a safe {@link Pageable} instance
     */
    public static Pageable buildPageable(int page, int size, String sortBy, String sortDir) {
        if (page < 0) {
            throw new BadRequestException("Page number cannot be negative");
        }
        if (size <= 0) {
            throw new BadRequestException("Page size must be greater than zero");
        }

        int clampedSize = Math.min(size, AppConstants.MAX_PAGE_SIZE);
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        return PageRequest.of(page, clampedSize, sort);
    }

    /**
     * Ensures a string is non-null and non-blank, throwing if not.
     */
    public static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException(fieldName + " must not be blank");
        }
    }
}
