package com.mabrikoli.service;

import com.mabrikoli.common.exception.BadRequestException;
import com.mabrikoli.common.exception.ResourceNotFoundException;
import com.mabrikoli.dto.category.CategoryRequest;
import com.mabrikoli.dto.category.CategoryResponse;
import com.mabrikoli.entity.ArtisanProfile;
import com.mabrikoli.entity.Category;
import com.mabrikoli.mapper.CategoryMapper;
import com.mabrikoli.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for Category management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * Creates a new category.
     *
     * @param request the category creation details
     * @return the created category response
     */
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new BadRequestException("Category with name '" + request.getName() + "' already exists");
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .iconUrl(request.getIconUrl())
                .build();

        category = categoryRepository.save(category);
        log.info("Created new category: {}", category.getName());

        return categoryMapper.toResponse(category);
    }

    /**
     * Retrieves all categories.
     *
     * @return a list of all category responses
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toResponseList(categories);
    }

    /**
     * Retrieves a single category by ID.
     *
     * @param id the category ID
     * @return the category response
     */
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = findCategoryById(id);
        return categoryMapper.toResponse(category);
    }

    /**
     * Updates an existing category.
     *
     * @param id      the ID of the category to update
     * @param request the updated category details
     * @return the updated category response
     */
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = findCategoryById(id);

        if (request.getName() != null && !request.getName().equals(category.getName())) {
            if (categoryRepository.existsByName(request.getName())) {
                throw new BadRequestException("Category with name '" + request.getName() + "' already exists");
            }
        }

        categoryMapper.updateEntityFromRequest(request, category);
        category = categoryRepository.save(category);
        log.info("Updated category with ID: {}", id);

        return categoryMapper.toResponse(category);
    }

    /**
     * Deletes a category by ID.
     * Dissociates the category from any artisan profiles before deleting to prevent DB constraint issues.
     *
     * @param id the ID of the category to delete
     */
    @Transactional
    public void deleteCategory(Long id) {
        Category category = findCategoryById(id);

        // Clear references in the bi-directional Many-to-Many association
        if (category.getArtisanProfiles() != null) {
            for (ArtisanProfile artisanProfile : category.getArtisanProfiles()) {
                artisanProfile.getCategories().remove(category);
            }
        }

        categoryRepository.delete(category);
        log.info("Deleted category with ID: {}", id);
    }

    // ── Internal Helpers ─────────────────────────────────────

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }
}
