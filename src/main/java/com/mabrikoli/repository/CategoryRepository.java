package com.mabrikoli.repository;

import com.mabrikoli.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Data access for {@link Category} entities.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Finds a category by its name.
     */
    Optional<Category> findByName(String name);

    /**
     * Checks if a category exists with the given name.
     */
    boolean existsByName(String name);
}
