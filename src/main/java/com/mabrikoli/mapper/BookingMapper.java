package com.mabrikoli.mapper;

import com.mabrikoli.dto.booking.BookingResponse;
import com.mabrikoli.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for {@link Booking} ↔ {@link BookingResponse} conversions.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "artisan.id", target = "artisanId")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(target = "clientName", expression = "java(booking.getClient().getFirstName() + \" \" + booking.getClient().getLastName())")
    @Mapping(target = "artisanName", expression = "java(booking.getArtisan().getUser().getFirstName() + \" \" + booking.getArtisan().getUser().getLastName())")
    BookingResponse toResponse(Booking booking);

    List<BookingResponse> toResponseList(List<Booking> bookings);
}
