package com.mabrikoli.mapper;

import com.mabrikoli.dto.profile.ArtisanProfileResponse;
import com.mabrikoli.entity.ArtisanProfile;
import com.mabrikoli.entity.Availability;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for {@link ArtisanProfile} ↔ {@link ArtisanProfileResponse} conversions.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {CategoryMapper.class})
public interface ArtisanProfileMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.profileImageUrl", target = "profilePhoto")
    ArtisanProfileResponse toResponse(ArtisanProfile profile);

    List<ArtisanProfileResponse> toResponseList(List<ArtisanProfile> profiles);

    ArtisanProfileResponse.AvailabilityInfo toAvailabilityInfo(Availability availability);
}
