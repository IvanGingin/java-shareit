package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(Long userId, BookingDto bookingDto);

    BookingDto updateBookingStatus(Long bookingId, Long userId, boolean approved);

    BookingDto getBooking(Long bookingId, Long userId);

    List<BookingDto> getUserBookings(Long userId, String state, int from, int size);

    List<BookingDto> getOwnerBookings(Long ownerId, String state, int from, int size);
}
