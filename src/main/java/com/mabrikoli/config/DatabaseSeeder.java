package com.mabrikoli.config;

import com.mabrikoli.entity.ArtisanApplication;
import com.mabrikoli.entity.ArtisanProfile;
import com.mabrikoli.entity.Category;
import com.mabrikoli.entity.User;
import com.mabrikoli.entity.VerificationDocument;
import com.mabrikoli.enums.ApplicationStatus;
import com.mabrikoli.enums.DocumentType;
import com.mabrikoli.enums.Role;
import com.mabrikoli.repository.ArtisanApplicationRepository;
import com.mabrikoli.repository.ArtisanProfileRepository;
import com.mabrikoli.repository.CategoryRepository;
import com.mabrikoli.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;

/**
 * CommandLineRunner database seeder that populates initial data for testing.
 * Seeds:
 * - 10 Categories
 * - 1 Admin
 * - 5 Clients
 * - 5 Approved Artisans (with profiles)
 * - 3 Pending Artisans (with pending applications)
 */
@Component
@Profile("!prod")
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ArtisanProfileRepository artisanProfileRepository;
    private final ArtisanApplicationRepository artisanApplicationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        ensureAdminExists();

        if (categoryRepository.count() == 0) {
            log.info("Starting database seeding...");

            // 1. Seed 10 Categories
            List<Category> categories = seedCategories();

            // 3. Seed 5 Clients
            seedClients();

            // 4. Seed 5 Approved Artisans
            seedApprovedArtisans(categories);

            // 5. Seed 3 Pending Artisans
            seedPendingArtisans(categories);

            log.info("Database seeding completed successfully!");
        } else {
            log.debug("Database already contains categories. Skipping other seed elements.");
        }
    }

    private List<Category> seedCategories() {
        log.info("Seeding 10 service categories...");
        List<Category> defaultCategories = Arrays.asList(
                Category.builder().name("Plumber").description("Plumbing installations, leaks fixing, and repairs.").build(),
                Category.builder().name("Electrician").description("Electrical system wiring, panels, and device installation.").build(),
                Category.builder().name("Carpenter").description("Woodworking, furniture assembly, and frame repair.").build(),
                Category.builder().name("Welder").description("Welding, metal fabrication, and structure repair.").build(),
                Category.builder().name("Painter").description("Interior/exterior painting, drywall, and plastering.").build(),
                Category.builder().name("Mechanic").description("Vehicle diagnostics, engine repairs, and maintenance.").build(),
                Category.builder().name("Gardener").description("Lawn mowing, landscape design, and garden maintenance.").build(),
                Category.builder().name("Mason").description("Bricklaying, concrete work, and structural masonry.").build(),
                Category.builder().name("Locksmith").description("Lock installations, emergency unlocking, and key duplicates.").build(),
                Category.builder().name("Cleaner").description("Residential, commercial, and post-construction cleaning.").build()
        );
        return categoryRepository.saveAll(defaultCategories);
    }

    private void ensureAdminExists() {
        if (!userRepository.existsByEmail("mohamed@admin.com")) {
            log.info("Admin account 'mohamed@admin.com' not found. Seeding/updating it now...");

            Optional<User> oldAdminOpt = userRepository.findByEmail("admin@mabrikoli.com");
            if (oldAdminOpt.isPresent()) {
                log.info("Updating existing 'admin@mabrikoli.com' to 'mohamed@admin.com' in-place...");
                User admin = oldAdminOpt.get();
                admin.setFirstName("mohamed");
                admin.setLastName("Admin");
                admin.setEmail("mohamed@admin.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                userRepository.save(admin);
                log.info("Admin account updated successfully.");
            } else {
                log.info("Creating brand new admin account 'mohamed@admin.com'...");
                User admin = User.builder()
                        .firstName("mohamed")
                        .lastName("Admin")
                        .email("mohamed@admin.com")
                        .password(passwordEncoder.encode("admin123"))
                        .phoneNumber("+1111111111")
                        .role(Role.ROLE_ADMIN)
                        .enabled(true)
                        .emailVerified(true)
                        .build();
                userRepository.save(admin);
                log.info("Admin account seeded successfully.");
            }
        }
    }

    private void seedClients() {
        log.info("Seeding 5 Client accounts...");
        List<User> clients = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            clients.add(User.builder()
                    .firstName("Client" + i)
                    .lastName("Doe")
                    .email("client" + i + "@mabrikoli.com")
                    .password(passwordEncoder.encode("password123"))
                    .phoneNumber("+21260000000" + i)
                    .role(Role.ROLE_CLIENT)
                    .enabled(true)
                    .emailVerified(true)
                    .build());
        }
        userRepository.saveAll(clients);
    }

    private void seedApprovedArtisans(List<Category> categories) {
        log.info("Seeding 5 Approved Artisan accounts and profiles...");
        String[] cities = {"Casablanca", "Rabat", "Marrakech", "Tangier", "Fes"};
        String[] bios = {
                "Professional plumber with 8 years of experience, ready for pipe fixing, installations, and drainage cleaning.",
                "Certified electrician, specialising in commercial electrical setups, troubleshooting, and smart home systems.",
                "Custom wooden furniture builder and installer with robust framing expertise.",
                "Expert welder focusing on structural gates, metal fences, and custom welding builds.",
                "Meticulous painter providing clean interior wall painting, finishes, and wall repairs."
        };

        for (int i = 1; i <= 5; i++) {
            User artisanUser = User.builder()
                    .firstName("Artisan" + i)
                    .lastName("Smith")
                    .email("artisan" + i + "@mabrikoli.com")
                    .password(passwordEncoder.encode("password123"))
                    .phoneNumber("+21261111111" + i)
                    .role(Role.ROLE_ARTISAN)
                    .enabled(true)
                    .emailVerified(true)
                    .profileImageUrl("https://api.dicebear.com/7.x/pixel-art/svg?seed=artisan" + i)
                    .build();

            artisanUser = userRepository.save(artisanUser);

            // Create Profile
            ArtisanProfile profile = ArtisanProfile.builder()
                    .user(artisanUser)
                    .bio(bios[i - 1])
                    .yearsOfExperience(3 + i)
                    .city(cities[i - 1])
                    .address(i * 10 + " Boulevard Mohammed V")
                    .hourlyPrice(15.0 * i)
                    .verified(true)
                    .available(true)
                    .build();

            // Link category (Artisan 1 gets Plumber, Artisan 2 gets Electrician, etc.)
            profile.addCategory(categories.get(i - 1));

            artisanProfileRepository.save(profile);
        }
    }

    private void seedPendingArtisans(List<Category> categories) {
        log.info("Seeding 3 Pending Artisan accounts and applications...");
        for (int i = 1; i <= 3; i++) {
            User pendingUser = User.builder()
                    .firstName("Pending" + i)
                    .lastName("Worker")
                    .email("pending" + i + "@mabrikoli.com")
                    .password(passwordEncoder.encode("password123"))
                    .phoneNumber("+21262222222" + i)
                    .role(Role.ROLE_CLIENT) // Status is client until application is approved
                    .enabled(true)
                    .emailVerified(true)
                    .profileImageUrl("https://api.dicebear.com/7.x/pixel-art/svg?seed=pending" + i)
                    .build();

            pendingUser = userRepository.save(pendingUser);

            // Create Application
            ArtisanApplication application = ArtisanApplication.builder()
                    .user(pendingUser)
                    .firstName(pendingUser.getFirstName())
                    .lastName(pendingUser.getLastName())
                    .phoneNumber(pendingUser.getPhoneNumber())
                    .city("Casablanca")
                    .yearsOfExperience(2 + i)
                    .description("I am submitting an application to join the platform as a professional service provider.")
                    .category(categories.get(categories.size() - i)) // Gardener, Mason, Locksmith, etc.
                    .personalPhotoUrl(pendingUser.getProfileImageUrl())
                    .status(ApplicationStatus.PENDING)
                    .build();

            // Add diploma attachment
            VerificationDocument diploma = VerificationDocument.builder()
                    .documentType(DocumentType.DIPLOMA)
                    .documentUrl("https://example.com/docs/diploma" + i + ".pdf")
                    .fileName("Diploma Certificate.pdf")
                    .fileSize(1024L * 100 * i)
                    .build();
            application.addDocument(diploma);

            // Add national ID attachment
            VerificationDocument nationalId = VerificationDocument.builder()
                    .documentType(DocumentType.ID_CARD)
                    .documentUrl("https://example.com/docs/id" + i + ".pdf")
                    .fileName("National Identity Card.pdf")
                    .fileSize(1024L * 50 * i)
                    .build();
            application.addDocument(nationalId);

            artisanApplicationRepository.save(application);
        }
    }
}
