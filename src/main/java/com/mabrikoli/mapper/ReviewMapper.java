package com.mabrikoli.mapper;

import com.mabrikoli.dto.review.ReviewResponse;
import com.mabrikoli.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for {@link Review} ↔ {@link ReviewResponse} conversions.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {

    @Mapping(source = "booking.id", target = "bookingId")
    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "artisan.id", target = "artisanId")
    @Mapping(target = "clientName", expression = "java(review.getClient().getFirstName() + \" \" + review.getClient().getLastName())")
    ReviewResponse toResponse(Review review);

    List<ReviewResponse> toResponseList(List<Review> reviews);
}
