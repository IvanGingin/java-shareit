package ru.practicum.shareit.booking.dao;
import ru.practicum.shareit.booking.model.Booking;
import java.util.List;

public interface BookingDao {
    Booking addBooking(Long userId, Booking booking);

    Booking updateBookingStatus(Long bookingId, boolean approved);

    Booking getBooking(Long bookingId);

    List<Booking> getUserBookings(Long userId, String state);

    List<Booking> getOwnerBookings(Long ownerId, String state);
}
