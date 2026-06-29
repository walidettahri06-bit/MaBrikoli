package com.mabrikoli.mapper;

import com.mabrikoli.dto.category.CategoryRequest;
import com.mabrikoli.dto.category.CategoryResponse;
import com.mabrikoli.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for {@link Category} ↔ {@link CategoryResponse} / {@link CategoryRequest} conversions.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);

    List<CategoryResponse> toResponseList(List<Category> categories);

    void updateEntityFromRequest(CategoryRequest request, @MappingTarget Category category);
}
