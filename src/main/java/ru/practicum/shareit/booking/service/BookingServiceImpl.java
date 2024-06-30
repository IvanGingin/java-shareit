package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto addBooking(Long userId, @Valid BookingDto bookingDto) {
        log.debug("Добавление бронирования пользователем с id={}, данными: {}", userId, bookingDto);
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        log.debug("Найден пользователь: {}, Найденная вещь: {}", booker, item);
        if (!item.getAvailable()) {
            log.error("Вещь с id={} недоступна для бронирования", item.getId());
            throw new ValidationException("Вещь недоступна для бронирования");
        }
        if (item.getOwner().getId().equals(userId)) {
            log.error("Пользователь с id={} пытается забронировать свою вещь с id={}", userId, item.getId());
            throw new NotFoundException("Нельзя забронировать свою вещь");
        }
        if (bookingDto.getStart().isEqual(bookingDto.getEnd()) || bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            log.error("Некорректные даты бронирования: start={}, end={}", bookingDto.getStart(), bookingDto.getEnd());
            throw new ValidationException("Дата начала бронирования должна быть раньше даты окончания");
        }
        Booking booking = BookingMapper.toModel(bookingDto, item, booker);
        booking.setStatus(BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        log.debug("Бронирование успешно добавлено: {}", savedBooking);
        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto updateBookingStatus(Long bookingId, Long userId, boolean approved) {
        log.debug("Обновление статуса бронирования с id={} пользователем с id={}, одобрено: {}", bookingId, userId, approved);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }
        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new ValidationException("Бронирование уже подтверждено");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        log.debug("Статус бронирования успешно обновлен: {}", updatedBooking);
        return BookingMapper.toBookingDto(updatedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBooking(Long bookingId, Long userId) {
        log.debug("Получение бронирования с id={} пользователем с id={}", bookingId, userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не имеет доступа к этому бронированию");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getUserBookings(Long userId, String state, int from, int size) {
        log.debug("Получение всех бронирований пользователя с id={} со статусом {}", userId, state);
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size);
        List<Booking> bookings;
        switch (state.toUpperCase()) {
            case "CURRENT":
                bookings = bookingRepository.findCurrentBookings(userId, now, pageable).getContent();
                break;
            case "PAST":
                bookings = bookingRepository.findPastBookings(userId, now, pageable).getContent();
                break;
            case "FUTURE":
                bookings = bookingRepository.findFutureBookings(userId, now, pageable).getContent();
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, pageable).getContent();
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageable).getContent();
                break;
            case "ALL":
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId, pageable).getContent();
                break;
            default:
                throw new UnsupportedException("{\"error\":\"Unknown state: " + state + "\"}", state);
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getOwnerBookings(Long ownerId, String state, int from, int size) {
        log.debug("Получение всех бронирований владельца с id={} со статусом {}", ownerId, state);
        userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Владелец не найден"));
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size);
        List<Booking> bookings;
        switch (state.toUpperCase()) {
            case "CURRENT":
                bookings = bookingRepository.findCurrentOwnerBookings(ownerId, now, pageable).getContent();
                break;
            case "PAST":
                bookings = bookingRepository.findPastOwnerBookings(ownerId, now, pageable).getContent();
                break;
            case "FUTURE":
                bookings = bookingRepository.findFutureOwnerBookings(ownerId, now, pageable).getContent();
                break;
            case "WAITING":
                bookings = bookingRepository.findOwnerBookingsByStatus(ownerId, BookingStatus.WAITING, pageable).getContent();
                break;
            case "REJECTED":
                bookings = bookingRepository.findOwnerBookingsByStatus(ownerId, BookingStatus.REJECTED, pageable).getContent();
                break;
            case "ALL":
                bookings = bookingRepository.findByItemOwnerId(ownerId, pageable).getContent();
                break;
            default:
                throw new UnsupportedException("{\"error\":\"Unknown state: " + state + "\"}", state);
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }
}
