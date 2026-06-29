package com.mabrikoli.mapper;

import com.mabrikoli.dto.application.ArtisanApplicationResponse;
import com.mabrikoli.entity.ArtisanApplication;
import com.mabrikoli.entity.VerificationDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for {@link ArtisanApplication} ↔ {@link ArtisanApplicationResponse} conversions.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {CategoryMapper.class})
public interface ArtisanApplicationMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "reviewedBy.id", target = "reviewedById")
    ArtisanApplicationResponse toResponse(ArtisanApplication application);

    List<ArtisanApplicationResponse> toResponseList(List<ArtisanApplication> applications);

    ArtisanApplicationResponse.DocumentInfo toDocumentInfo(VerificationDocument document);
}
