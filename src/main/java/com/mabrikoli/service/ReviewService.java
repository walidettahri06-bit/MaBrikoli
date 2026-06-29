package com.mabrikoli.service;

import com.mabrikoli.common.exception.BadRequestException;
import com.mabrikoli.common.exception.ResourceNotFoundException;
import com.mabrikoli.dto.review.ReviewRequest;
import com.mabrikoli.dto.review.ReviewResponse;
import com.mabrikoli.dto.review.ReviewUpdateRequest;
import com.mabrikoli.entity.ArtisanProfile;
import com.mabrikoli.entity.Booking;
import com.mabrikoli.entity.Review;
import com.mabrikoli.enums.BookingStatus;
import com.mabrikoli.mapper.ReviewMapper;
import com.mabrikoli.repository.ArtisanProfileRepository;
import com.mabrikoli.repository.BookingRepository;
import com.mabrikoli.repository.ReviewRepository;
import com.mabrikoli.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service managing customer reviews for completed bookings.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final ArtisanProfileRepository artisanProfileRepository;
    private final ReviewMapper reviewMapper;

    /**
     * Creates a new review for a completed booking.
     * Recalculates and updates the artisan's average rating in real-time.
     */
    @Transactional
    public ReviewResponse createReview(UserPrincipal clientPrincipal, ReviewRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", request.getBookingId()));

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new BadRequestException("Only completed bookings can be reviewed");
        }

        if (!booking.getClient().getId().equals(clientPrincipal.getId())) {
            throw new AccessDeniedException("You are not authorized to review this booking");
        }

        if (reviewRepository.existsByBookingId(request.getBookingId())) {
            throw new BadRequestException("This booking has already been reviewed");
        }

        Review review = Review.builder()
                .booking(booking)
                .client(booking.getClient())
                .artisan(booking.getArtisan())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        review = reviewRepository.save(review);

        // Update Artisan Rating Metrics
        ArtisanProfile artisan = booking.getArtisan();
        int oldTotal = artisan.getTotalReviews();
        int newTotal = oldTotal + 1;
        double newAverage = (artisan.getAverageRating() * oldTotal + request.getRating()) / newTotal;

        artisan.setTotalReviews(newTotal);
        artisan.setAverageRating(newAverage);
        artisanProfileRepository.save(artisan);

        log.info("Created review ID {} for booking ID {}. Updated artisan ID {} average rating to {} (total reviews: {})",
                review.getId(), booking.getId(), artisan.getId(), newAverage, newTotal);

        return reviewMapper.toResponse(review);
    }

    /**
     * Updates an existing review (rating and/or comment).
     * Re-calculates and updates the artisan's average rating in real-time.
     */
    @Transactional
    public ReviewResponse updateReview(Long id, ReviewUpdateRequest request, UserPrincipal clientPrincipal) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

        if (!review.getClient().getId().equals(clientPrincipal.getId())) {
            throw new AccessDeniedException("You are not authorized to update this review");
        }

        ArtisanProfile artisan = review.getArtisan();
        int oldRating = review.getRating();
        int newRating = request.getRating();

        // Recalculate average rating based on change in rating
        if (oldRating != newRating) {
            double totalScore = artisan.getAverageRating() * artisan.getTotalReviews();
            double newAverage = (totalScore - oldRating + newRating) / artisan.getTotalReviews();
            artisan.setAverageRating(newAverage);
            artisanProfileRepository.save(artisan);
        }

        review.setRating(newRating);
        review.setComment(request.getComment());
        review = reviewRepository.save(review);

        log.info("Updated review ID {}. Recalculated artisan ID {} average rating to {}", 
                id, artisan.getId(), artisan.getAverageRating());

        return reviewMapper.toResponse(review);
    }

    /**
     * Deletes a review.
     * Decrements the artisan's rating metrics.
     */
    @Transactional
    public void deleteReview(Long id, UserPrincipal principal) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

        boolean isOwner = review.getClient().getId().equals(principal.getId());
        boolean isAdmin = principal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to delete this review");
        }

        ArtisanProfile artisan = review.getArtisan();
        int oldRating = review.getRating();
        int newTotal = artisan.getTotalReviews() - 1;

        if (newTotal > 0) {
            double totalScore = artisan.getAverageRating() * artisan.getTotalReviews();
            double newAverage = (totalScore - oldRating) / newTotal;
            artisan.setAverageRating(newAverage);
        } else {
            artisan.setAverageRating(0.0);
        }
        artisan.setTotalReviews(newTotal);
        artisanProfileRepository.save(artisan);

        reviewRepository.delete(review);
        log.info("Deleted review ID {}. Recalculated artisan ID {} average rating to {} (total reviews: {})", 
                id, artisan.getId(), artisan.getAverageRating(), newTotal);
    }

    /**
     * Retrieves all reviews written for a specific artisan.
     */
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsForArtisan(Long artisanId) {
        List<Review> reviews = reviewRepository.findByArtisanId(artisanId);
        return reviewMapper.toResponseList(reviews);
    }

    /**
     * Retrieves a single review by ID.
     */
    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));
        return reviewMapper.toResponse(review);
    }
}
