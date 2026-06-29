package com.mabrikoli.entity;

import com.mabrikoli.enums.DocumentType;
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

/**
 * A document uploaded as part of an {@link ArtisanApplication}.
 * <p>
 * Examples: national ID, professional diploma, trade certificate,
 * proof of address. Lifecycle is bound to the parent application
 * (cascade ALL + orphanRemoval).
 */
@Entity
@Table(
        name = "verification_documents",
        indexes = {
                @Index(name = "idx_verdocs_application", columnList = "application_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "application")
public class VerificationDocument extends BaseEntity {

    /**
     * The application this document belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private ArtisanApplication application;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 30)
    private DocumentType documentType;

    @Column(name = "document_url", nullable = false)
    private String documentUrl;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_size")
    private long fileSize;
}
