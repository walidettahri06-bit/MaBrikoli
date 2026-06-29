package com.mabrikoli.entity;

import com.mabrikoli.enums.ApplicationStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * An artisan's request to join the platform.
 * <p>
 * Contains personal information, motivation, and attached
 * {@link VerificationDocument}s. Only an admin can approve or reject.
 * A user may resubmit if a previous application was rejected.
 */
@Entity
@Table(
        name = "artisan_applications",
        indexes = {
                @Index(name = "idx_applications_user", columnList = "user_id"),
                @Index(name = "idx_applications_status", columnList = "status"),
                @Index(name = "idx_applications_user_status", columnList = "user_id, status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "reviewedBy", "documents", "category"})
public class ArtisanApplication extends BaseEntity {

    // ── Applicant ────────────────────────────────────────────

    /**
     * The user who submitted this application.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ── Application Data ─────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(name = "years_of_experience", nullable = false)
    private int yearsOfExperience;

    @Column(nullable = false, length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "personal_photo_url", nullable = false)
    private String personalPhotoUrl;

    // Legacy fields mapped/retained for compatibility if needed
    @Column(length = 1000)
    private String motivation;

    @Column(name = "professional_experience", length = 2000)
    private String professionalExperience;

    // ── Admin Review ─────────────────────────────────────────

    /**
     * The admin who reviewed (approved/rejected) this application.
     * Null while {@link #status} is {@code PENDING}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Column(name = "admin_notes", length = 1000)
    private String adminNotes;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    // ── Verification Documents ───────────────────────────────

    /**
     * Supporting documents attached to this application.
     * Cascade ALL + orphanRemoval: documents are a composition of the application.
     */
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<VerificationDocument> documents = new ArrayList<>();

    // ── Helper Methods ───────────────────────────────────────

    public void addDocument(VerificationDocument document) {
        documents.add(document);
        document.setApplication(this);
    }

    public void removeDocument(VerificationDocument document) {
        documents.remove(document);
        document.setApplication(null);
    }
}
