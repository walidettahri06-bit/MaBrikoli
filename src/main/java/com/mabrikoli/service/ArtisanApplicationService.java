package com.mabrikoli.service;

import com.mabrikoli.common.exception.BadRequestException;
import com.mabrikoli.common.exception.ResourceNotFoundException;
import com.mabrikoli.dto.application.AdminReviewRequest;
import com.mabrikoli.dto.application.ArtisanApplicationRequest;
import com.mabrikoli.dto.application.ArtisanApplicationResponse;
import com.mabrikoli.entity.ArtisanApplication;
import com.mabrikoli.entity.ArtisanProfile;
import com.mabrikoli.entity.Category;
import com.mabrikoli.entity.User;
import com.mabrikoli.entity.VerificationDocument;
import com.mabrikoli.enums.ApplicationStatus;
import com.mabrikoli.enums.DocumentType;
import com.mabrikoli.enums.Role;
import com.mabrikoli.mapper.ArtisanApplicationMapper;
import com.mabrikoli.repository.ArtisanApplicationRepository;
import com.mabrikoli.repository.ArtisanProfileRepository;
import com.mabrikoli.repository.CategoryRepository;
import com.mabrikoli.repository.UserRepository;
import com.mabrikoli.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing Artisan Applications and their reviews.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArtisanApplicationService {

    private final ArtisanApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final ArtisanProfileRepository artisanProfileRepository;
    private final CategoryRepository categoryRepository;
    private final ArtisanApplicationMapper applicationMapper;

    /**
     * Submits a new artisan application for the authenticated user.
     */
    @Transactional
    public ArtisanApplicationResponse submitApplication(UserPrincipal applicant, ArtisanApplicationRequest request) {
        User user = userRepository.findById(applicant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", applicant.getId()));

        if (user.getRole() == Role.ROLE_ARTISAN) {
            throw new BadRequestException("You are already registered as an artisan");
        }

        if (applicationRepository.existsByUserIdAndStatus(user.getId(), ApplicationStatus.PENDING)) {
            throw new BadRequestException("You already have a pending artisan application");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        // Build application
        ArtisanApplication application = ArtisanApplication.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .city(request.getCity())
                .yearsOfExperience(request.getYearsOfExperience())
                .description(request.getDescription())
                .category(category)
                .personalPhotoUrl(request.getPersonalPhotoUrl())
                .status(ApplicationStatus.PENDING)
                .build();

        // Attach diploma
        VerificationDocument diploma = VerificationDocument.builder()
                .documentType(DocumentType.DIPLOMA)
                .documentUrl(request.getDiplomaFileUrl())
                .fileName("Diploma")
                .fileSize(0)
                .build();
        application.addDocument(diploma);

        // Attach national ID
        VerificationDocument nationalId = VerificationDocument.builder()
                .documentType(DocumentType.ID_CARD)
                .documentUrl(request.getNationalIdFileUrl())
                .fileName("National ID")
                .fileSize(0)
                .build();
        application.addDocument(nationalId);

        application = applicationRepository.save(application);
        log.info("User ID {} submitted an artisan application. Application ID: {}", user.getId(), application.getId());

        return applicationMapper.toResponse(application);
    }

    /**
     * Retrieves all artisan applications (Admin view).
     */
    @Transactional(readOnly = true)
    public List<ArtisanApplicationResponse> getAllApplications() {
        List<ArtisanApplication> applications = applicationRepository.findAll();
        return applicationMapper.toResponseList(applications);
    }

    /**
     * Retrieves a single application by ID (Admin/User view).
     */
    @Transactional(readOnly = true)
    public ArtisanApplicationResponse getApplicationById(Long id) {
        ArtisanApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ArtisanApplication", "id", id));
        return applicationMapper.toResponse(application);
    }

    /**
     * Retrieves the current user's applications history.
     */
    @Transactional(readOnly = true)
    public List<ArtisanApplicationResponse> getMyApplications(UserPrincipal applicant) {
        List<ArtisanApplication> applications = applicationRepository.findByUserId(applicant.getId());
        return applicationMapper.toResponseList(applications);
    }

    /**
     * Approves an artisan application.
     * Transitions user role and creates artisan profile.
     */
    @Transactional
    public ArtisanApplicationResponse approveApplication(Long id, UserPrincipal adminPrincipal) {
        ArtisanApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ArtisanApplication", "id", id));

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new BadRequestException("Application is already processed and is " + application.getStatus());
        }

        User admin = userRepository.findById(adminPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", adminPrincipal.getId()));

        User applicant = application.getUser();

        // 1. Update application status
        application.setStatus(ApplicationStatus.APPROVED);
        application.setReviewedBy(admin);
        application.setReviewedAt(LocalDateTime.now());
        application.setAdminNotes("Approved");

        // 2. Transition User to Artisan role
        applicant.setRole(Role.ROLE_ARTISAN);
        applicant.setFirstName(application.getFirstName());
        applicant.setLastName(application.getLastName());
        applicant.setPhoneNumber(application.getPhoneNumber());
        applicant.setProfileImageUrl(application.getPersonalPhotoUrl());
        userRepository.save(applicant);

        // 3. Create or update ArtisanProfile
        ArtisanProfile profile = artisanProfileRepository.findByUserId(applicant.getId())
                .orElse(new ArtisanProfile());

        profile.setUser(applicant);
        profile.setBio(application.getDescription());
        profile.setYearsOfExperience(application.getYearsOfExperience());
        profile.setCity(application.getCity());
        profile.setVerified(true);
        profile.setAvailable(true);
        profile.addCategory(application.getCategory());

        artisanProfileRepository.save(profile);

        application = applicationRepository.save(application);
        log.info("Application ID {} approved by admin ID {}. Profile created for artisan user ID {}", 
                id, admin.getId(), applicant.getId());

        return applicationMapper.toResponse(application);
    }

    /**
     * Rejects an artisan application (with reason).
     */
    @Transactional
    public ArtisanApplicationResponse rejectApplication(Long id, AdminReviewRequest reviewRequest, UserPrincipal adminPrincipal) {
        ArtisanApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ArtisanApplication", "id", id));

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new BadRequestException("Application is already processed and is " + application.getStatus());
        }

        User admin = userRepository.findById(adminPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", adminPrincipal.getId()));

        application.setStatus(ApplicationStatus.REJECTED);
        application.setReviewedBy(admin);
        application.setReviewedAt(LocalDateTime.now());
        application.setAdminNotes(reviewRequest.getReason());

        application = applicationRepository.save(application);
        log.info("Application ID {} rejected by admin ID {}. Reason: {}", id, admin.getId(), reviewRequest.getReason());

        return applicationMapper.toResponse(application);
    }
}
