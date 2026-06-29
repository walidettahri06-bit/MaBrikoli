package com.mabrikoli.service;

import com.mabrikoli.common.exception.ResourceNotFoundException;
import com.mabrikoli.dto.profile.ArtisanProfileRequest;
import com.mabrikoli.dto.profile.ArtisanProfileResponse;
import com.mabrikoli.dto.profile.AvailabilityRequest;
import com.mabrikoli.entity.ArtisanProfile;
import com.mabrikoli.entity.Availability;
import com.mabrikoli.entity.Category;
import com.mabrikoli.mapper.ArtisanProfileMapper;
import com.mabrikoli.repository.ArtisanProfileRepository;
import com.mabrikoli.repository.CategoryRepository;
import com.mabrikoli.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service managing Artisan Profiles and schedules.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArtisanProfileService {

    private final ArtisanProfileRepository artisanProfileRepository;
    private final CategoryRepository categoryRepository;
    private final ArtisanProfileMapper artisanProfileMapper;

    /**
     * Retrieves all artisan profiles.
     */
    @Transactional(readOnly = true)
    public List<ArtisanProfileResponse> getAllProfiles() {
        List<ArtisanProfile> profiles = artisanProfileRepository.findAll();
        return artisanProfileMapper.toResponseList(profiles);
    }

    /**
     * Retrieves a single profile by ID.
     */
    @Transactional(readOnly = true)
    public ArtisanProfileResponse getProfileById(Long id) {
        ArtisanProfile profile = artisanProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ArtisanProfile", "id", id));
        return artisanProfileMapper.toResponse(profile);
    }

    /**
     * Retrieves the profile of the currently authenticated artisan.
     */
    @Transactional(readOnly = true)
    public ArtisanProfileResponse getMyProfile(UserPrincipal principal) {
        ArtisanProfile profile = artisanProfileRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("ArtisanProfile", "userId", principal.getId()));
        return artisanProfileMapper.toResponse(profile);
    }

    /**
     * Updates the currently authenticated artisan's profile.
     * Fully replaces category specialties and availability slots.
     */
    @Transactional
    public ArtisanProfileResponse updateProfile(UserPrincipal principal, ArtisanProfileRequest request) {
        ArtisanProfile profile = artisanProfileRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("ArtisanProfile", "userId", principal.getId()));

        // Update basic fields
        profile.setBio(request.getBio());
        profile.setYearsOfExperience(request.getYearsOfExperience());
        profile.setCity(request.getCity());
        profile.setAddress(request.getAddress());
        profile.setHourlyPrice(request.getHourlyPrice());
        profile.setAvailable(request.isAvailable());

        // Update Categories
        if (request.getCategoryIds() != null) {
            List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
            profile.setCategories(new HashSet<>(categories));
        }

        // Update Availabilities (Clear existing slots and add new ones)
        profile.getAvailabilities().clear();
        if (request.getAvailabilities() != null) {
            for (AvailabilityRequest slotRequest : request.getAvailabilities()) {
                Availability slot = Availability.builder()
                        .artisanProfile(profile)
                        .dayOfWeek(slotRequest.getDayOfWeek())
                        .startTime(slotRequest.getStartTime())
                        .endTime(slotRequest.getEndTime())
                        .available(slotRequest.isAvailable())
                        .build();
                profile.getAvailabilities().add(slot);
            }
        }

        profile = artisanProfileRepository.save(profile);
        log.info("Updated artisan profile for user ID: {}", principal.getId());

        return artisanProfileMapper.toResponse(profile);
    }

    /**
     * Deletes a profile by ID (Admin only).
     */
    @Transactional
    public void deleteProfile(Long id) {
        ArtisanProfile profile = artisanProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ArtisanProfile", "id", id));
        artisanProfileRepository.delete(profile);
        log.info("Deleted artisan profile with ID: {}", id);
    }
}
