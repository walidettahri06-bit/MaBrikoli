package com.mabrikoli.mapper;

import com.mabrikoli.dto.user.UserResponse;
import com.mabrikoli.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for {@link User} ↔ {@link UserResponse} conversions.
 * <p>
 * Registered as a Spring bean via {@code componentModel = "spring"} (set globally
 * in the Maven compiler plugin).
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    /**
     * Maps a User entity to a UserResponse DTO.
     * Password is never mapped.
     */
    UserResponse toResponse(User user);

    /**
     * Maps a list of User entities to a list of UserResponse DTOs.
     */
    List<UserResponse> toResponseList(List<User> users);
}
