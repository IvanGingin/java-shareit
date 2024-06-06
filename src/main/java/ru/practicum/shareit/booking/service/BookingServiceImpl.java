package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingDao bookingDao;
    private final ItemDao itemDao;
    private final UserDao userDao;

    @Override
    public BookingDto addBooking(Long userId, @Valid BookingDto bookingDto) {
        log.debug("Добавление бронирования пользователем с id={}, данными: {}", userId, bookingDto);
        try {
            Item item = itemDao.getItem(bookingDto.getItemId());
            User booker = userDao.getUser(userId);

            if (item == null || booker == null || item.getOwner().getId().equals(userId)) {
                throw new IllegalArgumentException("Некорректные данные бронирования");
            }

            Booking booking = BookingMapper.toModel(bookingDto, item, booker);
            BookingDto createdBooking = BookingMapper.toBookingDto(bookingDao.addBooking(userId, booking));
            log.debug("Бронирование успешно добавлено: {}", createdBooking);
            return createdBooking;
        } catch (Exception e) {
            log.error("Ошибка при добавлении бронирования: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public BookingDto updateBookingStatus(Long bookingId, Long userId, boolean approved) {
        log.debug("Обновление статуса бронирования с id={} пользователем с id={}, одобрено: {}", bookingId, userId, approved);
        try {
            Booking booking = bookingDao.getBooking(bookingId);
            if (booking == null || !booking.getItem().getOwner().getId().equals(userId)) {
                throw new IllegalArgumentException("Некорректные данные бронирования");
            }

            BookingDto updatedBooking = BookingMapper.toBookingDto(bookingDao.updateBookingStatus(bookingId, approved));
            log.debug("Статус бронирования с id={} успешно обновлен: {}", bookingId, updatedBooking);
            return updatedBooking;
        } catch (Exception e) {
            log.error("Ошибка при обновлении статуса бронирования: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public BookingDto getBooking(Long bookingId, Long userId) {
        log.debug("Получение бронирования с id={} пользователем с id={}", bookingId, userId);
        try {
            Booking booking = bookingDao.getBooking(bookingId);
            if (booking == null || (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId))) {
                throw new IllegalArgumentException("Некорректные данные бронирования");
            }

            BookingDto bookingDto = BookingMapper.toBookingDto(booking);
            log.debug("Бронирование с id={} успешно получено: {}", bookingId, bookingDto);
            return bookingDto;
        } catch (Exception e) {
            log.error("Ошибка при получении бронирования: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, String state) {
        log.debug("Получение всех бронирований пользователя с id={} со статусом {}", userId, state);
        try {
            List<Booking> bookings = bookingDao.getUserBookings(userId, state);
            List<BookingDto> bookingDtoList = new ArrayList<>();
            for (Booking booking : bookings) {
                bookingDtoList.add(BookingMapper.toBookingDto(booking));
            }
            log.debug("Всего бронирований найдено: {}", bookingDtoList.size());
            return bookingDtoList;
        } catch (Exception e) {
            log.error("Ошибка при получении бронирований пользователя: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, String state) {
        log.debug("Получение всех бронирований владельца с id={} со статусом {}", ownerId, state);
        try {
            List<Booking> bookings = bookingDao.getOwnerBookings(ownerId, state);
            List<BookingDto> bookingDtoList = new ArrayList<>();
            for (Booking booking : bookings) {
                bookingDtoList.add(BookingMapper.toBookingDto(booking));
            }
            log.debug("Всего бронирований найдено: {}", bookingDtoList.size());
            return bookingDtoList;
        } catch (Exception e) {
            log.error("Ошибка при получении бронирований владельца: {}", e.getMessage(), e);
            throw e;
        }
    }
}
