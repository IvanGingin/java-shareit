package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.util.Constants.CONST_SHARED_USER_ID;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader(CONST_SHARED_USER_ID) Long userId, @Valid @RequestBody BookingDto bookingDto) {
        log.debug("Получен запрос на добавление бронирования от пользователя с id={}, данными: {}", userId, bookingDto);
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@PathVariable Long bookingId, @RequestHeader(CONST_SHARED_USER_ID) Long userId, @Valid @RequestParam boolean approved) {
        log.debug("Получен запрос на обновление статуса бронирования с id={} от пользователя с id={}, одобрено: {}", bookingId, userId, approved);
        return bookingService.updateBookingStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId, @RequestHeader(CONST_SHARED_USER_ID) Long userId) {
        log.debug("Получен запрос на получение бронирования с id={} от пользователя с id={}", bookingId, userId);
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader(CONST_SHARED_USER_ID) Long userId,
                                            @RequestParam(defaultValue = "ALL") String state,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                            @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("Получен запрос на получение всех бронирований пользователя с id={} со статусом {}, с параметрами from={}, size={}", userId, state, from, size);
        return bookingService.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader(CONST_SHARED_USER_ID) Long ownerId,
                                             @RequestParam(defaultValue = "ALL") String state,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("Получен запрос на получение всех бронирований владельца с id={} со статусом {}, с параметрами from={}, size={}", ownerId, state, from, size);
        return bookingService.getOwnerBookings(ownerId, state, from, size);
    }
}
