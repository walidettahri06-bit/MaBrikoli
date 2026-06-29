package com.mabrikoli.dto.application;

import com.mabrikoli.dto.category.CategoryResponse;
import com.mabrikoli.enums.ApplicationStatus;
import com.mabrikoli.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response payload representing an artisan application.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtisanApplicationResponse {

    private Long id;
    private Long userId;
    private ApplicationStatus status;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String city;
    private int yearsOfExperience;
    private String description;
    private CategoryResponse category;
    private String personalPhotoUrl;
    private String adminNotes;
    private Long reviewedById;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<DocumentInfo> documents;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentInfo {
        private Long id;
        private DocumentType documentType;
        private String documentUrl;
        private String fileName;
        private long fileSize;
    }
}
