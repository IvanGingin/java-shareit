package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.Constants;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader(Constants.CONST_SHARED_USER_ID) Long userId, @Valid @RequestBody BookingDto bookingDto) {
        log.debug("Получен запрос на добавление бронирования от пользователя с id={}, данными: {}", userId, bookingDto);
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@PathVariable Long bookingId, @RequestHeader(Constants.CONST_SHARED_USER_ID) Long userId, @RequestParam boolean approved) {
        log.debug("Получен запрос на обновление статуса бронирования с id={} от пользователя с id={}, одобрено: {}", bookingId, userId, approved);
        return bookingService.updateBookingStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId, @RequestHeader(Constants.CONST_SHARED_USER_ID) Long userId) {
        log.debug("Получен запрос на получение бронирования с id={} от пользователя с id={}", bookingId, userId);
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader(Constants.CONST_SHARED_USER_ID) Long userId, @RequestParam String state) {
        log.debug("Получен запрос на получение всех бронирований пользователя с id={} со статусом {}", userId, state);
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader(Constants.CONST_SHARED_USER_ID) Long ownerId, @RequestParam String state) {
        log.debug("Получен запрос на получение всех бронирований владельца с id={} со статусом {}", ownerId, state);
        return bookingService.getOwnerBookings(ownerId, state);
    }
}
