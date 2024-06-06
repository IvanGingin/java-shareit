package ru.practicum.shareit.booking.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.*;

@Slf4j
@Repository
public class InMemoryBookingDao implements BookingDao {
    private final Map<Long, Booking> bookings = new HashMap<>();
    private long bookingIdGenerator = 0;

    @Override
    public Booking addBooking(Long userId, Booking booking) {
        log.debug("Добавление бронирования с данными: {}", booking);
        long bookingId = ++bookingIdGenerator;
        booking.setId(bookingId);
        booking.setBookingStatus(BookingStatus.WAITING);
        bookings.put(bookingId, booking);
        log.debug("Бронирование успешно добавлено с id={}", bookingId);
        return booking;
    }

    @Override
    public Booking updateBookingStatus(Long bookingId, boolean approved) {
        log.debug("Обновление статуса бронирования с id={} на {}", bookingId, approved ? "APPROVED" : "REJECTED");
        Booking booking = bookings.get(bookingId);
        if (booking != null) {
            booking.setBookingStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
            bookings.put(bookingId, booking);
            log.debug("Статус бронирования с id={} успешно обновлен на {}", bookingId, booking.getBookingStatus());
            return booking;
        } else {
            log.error("Бронирование с id={} не найдено!", bookingId);
            return null;
        }
    }

    @Override
    public Booking getBooking(Long bookingId) {
        log.debug("Получение бронирования с id={}", bookingId);
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            log.error("Бронирование с id={} не найдено!", bookingId);
        }
        return booking;
    }

    @Override
    public List<Booking> getUserBookings(Long userId, String state) {
        log.debug("Получение всех бронирований пользователя с id={} со статусом {}", userId, state);
        List<Booking> result = new ArrayList<>();
        for (Booking booking : bookings.values()) {
            if (booking.getBooker().getId().equals(userId) && booking.getBookingStatus().toString().equalsIgnoreCase(state)) {
                result.add(booking);
            }
        }
        return result;
    }

    @Override
    public List<Booking> getOwnerBookings(Long ownerId, String state) {
        log.debug("Получение всех бронирований владельца с id={} со статусом {}", ownerId, state);
        List<Booking> result = new ArrayList<>();
        for (Booking booking : bookings.values()) {
            if (booking.getItem().getOwner().getId().equals(ownerId) && booking.getBookingStatus().toString().equalsIgnoreCase(state)) {
                result.add(booking);
            }
        }
        return result;
    }
}
