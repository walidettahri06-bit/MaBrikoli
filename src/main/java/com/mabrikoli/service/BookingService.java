package com.mabrikoli.service;

import com.mabrikoli.common.exception.BadRequestException;
import com.mabrikoli.common.exception.ResourceNotFoundException;
import com.mabrikoli.dto.booking.BookingNotesRequest;
import com.mabrikoli.dto.booking.BookingRequest;
import com.mabrikoli.dto.booking.BookingResponse;
import com.mabrikoli.entity.ArtisanProfile;
import com.mabrikoli.entity.Booking;
import com.mabrikoli.entity.Category;
import com.mabrikoli.entity.User;
import com.mabrikoli.enums.BookingStatus;
import com.mabrikoli.mapper.BookingMapper;
import com.mabrikoli.repository.ArtisanProfileRepository;
import com.mabrikoli.repository.BookingRepository;
import com.mabrikoli.repository.CategoryRepository;
import com.mabrikoli.repository.UserRepository;
import com.mabrikoli.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service managing service bookings and status workflows.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ArtisanProfileRepository artisanProfileRepository;
    private final CategoryRepository categoryRepository;
    private final BookingMapper bookingMapper;

    /**
     * Creates a new booking in PENDING status.
     */
    @Transactional
    public BookingResponse createBooking(UserPrincipal clientPrincipal, BookingRequest request) {
        User client = userRepository.findById(clientPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", clientPrincipal.getId()));

        ArtisanProfile artisan = artisanProfileRepository.findById(request.getArtisanId())
                .orElseThrow(() -> new ResourceNotFoundException("ArtisanProfile", "id", request.getArtisanId()));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        // Business Validation
        if (artisan.getUser().getId().equals(client.getId())) {
            throw new BadRequestException("You cannot book your own services");
        }

        if (!artisan.isVerified()) {
            throw new BadRequestException("This artisan profile is not verified yet");
        }

        if (!artisan.isAvailable()) {
            throw new BadRequestException("This artisan is currently unavailable for new bookings");
        }

        Booking booking = Booking.builder()
                .client(client)
                .artisan(artisan)
                .category(category)
                .description(request.getDescription())
                .address(request.getAddress())
                .city(request.getCity())
                .bookingDate(request.getBookingDate())
                .preferredTime(request.getPreferredTime())
                .clientNotes(request.getClientNotes())
                .status(BookingStatus.PENDING)
                .build();

        if (artisan.getHourlyPrice() != null) {
            booking.setEstimatedPrice(BigDecimal.valueOf(artisan.getHourlyPrice()));
        }

        booking = bookingRepository.save(booking);
        log.info("Booking created in PENDING status. Booking ID: {} (Client: {}, Artisan: {})", 
                booking.getId(), client.getId(), artisan.getId());

        return bookingMapper.toResponse(booking);
    }

    /**
     * Artisan accepts a PENDING booking.
     */
    @Transactional
    public BookingResponse acceptBooking(Long id, UserPrincipal artisanPrincipal) {
        Booking booking = findBookingById(id);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Booking is already processed and is " + booking.getStatus());
        }

        // Authorization check
        if (!booking.getArtisan().getUser().getId().equals(artisanPrincipal.getId())) {
            throw new AccessDeniedException("You are not authorized to accept this booking");
        }

        booking.setStatus(BookingStatus.ACCEPTED);
        booking = bookingRepository.save(booking);
        log.info("Booking ID {} accepted by artisan ID {}", id, artisanPrincipal.getId());

        return bookingMapper.toResponse(booking);
    }

    /**
     * Artisan rejects a PENDING booking (optionally with notes).
     */
    @Transactional
    public BookingResponse rejectBooking(Long id, BookingNotesRequest notesRequest, UserPrincipal artisanPrincipal) {
        Booking booking = findBookingById(id);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Booking is already processed and is " + booking.getStatus());
        }

        // Authorization check
        if (!booking.getArtisan().getUser().getId().equals(artisanPrincipal.getId())) {
            throw new AccessDeniedException("You are not authorized to reject this booking");
        }

        booking.setStatus(BookingStatus.REJECTED);
        booking.setArtisanNotes(notesRequest.getReason());
        booking = bookingRepository.save(booking);
        log.info("Booking ID {} rejected by artisan ID {}", id, artisanPrincipal.getId());

        return bookingMapper.toResponse(booking);
    }

    /**
     * Artisan completes an ACCEPTED booking.
     */
    @Transactional
    public BookingResponse completeBooking(Long id, UserPrincipal artisanPrincipal) {
        Booking booking = findBookingById(id);

        if (booking.getStatus() != BookingStatus.ACCEPTED) {
            throw new BadRequestException("Only accepted bookings can be marked as completed");
        }

        // Authorization check
        if (!booking.getArtisan().getUser().getId().equals(artisanPrincipal.getId())) {
            throw new AccessDeniedException("You are not authorized to complete this booking");
        }

        booking.setStatus(BookingStatus.COMPLETED);
        booking.setCompletedAt(LocalDateTime.now());
        booking = bookingRepository.save(booking);
        log.info("Booking ID {} completed by artisan ID {}", id, artisanPrincipal.getId());

        return bookingMapper.toResponse(booking);
    }

    /**
     * Client or Artisan cancels a PENDING or ACCEPTED booking.
     */
    @Transactional
    public BookingResponse cancelBooking(Long id, BookingNotesRequest notesRequest, UserPrincipal principal) {
        Booking booking = findBookingById(id);

        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.ACCEPTED) {
            throw new BadRequestException("Only pending or accepted bookings can be cancelled");
        }

        // Authorization check
        boolean isClient = booking.getClient().getId().equals(principal.getId());
        boolean isArtisan = booking.getArtisan().getUser().getId().equals(principal.getId());
        boolean isAdmin = principal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isClient && !isArtisan && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to cancel this booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancellationReason(notesRequest.getReason());
        booking.setCancelledAt(LocalDateTime.now());
        booking = bookingRepository.save(booking);
        log.info("Booking ID {} cancelled by user ID {}", id, principal.getId());

        return bookingMapper.toResponse(booking);
    }

    /**
     * Retrieves a booking by ID (only visible to participants and admin).
     */
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id, UserPrincipal principal) {
        Booking booking = findBookingById(id);

        boolean isClient = booking.getClient().getId().equals(principal.getId());
        boolean isArtisan = booking.getArtisan().getUser().getId().equals(principal.getId());
        boolean isAdmin = principal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isClient && !isArtisan && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to view this booking");
        }

        return bookingMapper.toResponse(booking);
    }

    /**
     * Retrieves bookings requested by currently authenticated client.
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getClientBookings(UserPrincipal client) {
        List<Booking> bookings = bookingRepository.findByClientId(client.getId());
        return bookingMapper.toResponseList(bookings);
    }

    /**
     * Retrieves bookings assigned to currently authenticated artisan.
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getArtisanBookings(UserPrincipal artisan) {
        List<Booking> bookings = bookingRepository.findByArtisanUserId(artisan.getId());
        return bookingMapper.toResponseList(bookings);
    }

    /**
     * Retrieves all bookings (Admin only).
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return bookingMapper.toResponseList(bookings);
    }

    // ── Internal Helpers ─────────────────────────────────────

    private Booking findBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
    }
}
