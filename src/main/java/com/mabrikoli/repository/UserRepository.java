package com.mabrikoli.repository;

import com.mabrikoli.entity.User;
import com.mabrikoli.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Data access for {@link User} entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by email address (used for authentication).
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether an email is already registered.
     */
    boolean existsByEmail(String email);

    /**
     * Checks whether a phone number is already registered.
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * Counts users with a specific role.
     */
    long countByRole(Role role);
}
