package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constants.CONST_SHARED_USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> addBooking(@RequestHeader(CONST_SHARED_USER_ID) long userId,
											 @Valid @RequestBody BookingDto bookingDto) {
		log.debug("Получен запрос на добавление бронирования от пользователя с id={}, данными: {}", userId, bookingDto);
		return bookingClient.addBooking(userId, bookingDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> updateBooking(@PathVariable Long bookingId,
												@RequestHeader(CONST_SHARED_USER_ID) long userId,
												@RequestParam boolean approved) {
		log.debug("Получен запрос на обновление статуса бронирования с id={} от пользователя с id={}, одобрено: {}", bookingId, userId, approved);
		return bookingClient.updateBooking(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@PathVariable Long bookingId,
											 @RequestHeader(CONST_SHARED_USER_ID) long userId) {
		log.debug("Получен запрос на получение бронирования с id={} от пользователя с id={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader(CONST_SHARED_USER_ID) long userId,
											  @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
											  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
											  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		log.debug("Получен запрос на получение всех бронирований пользователя с id={} со статусом {}, с параметрами from={}, size={}", userId, stateParam, from, size);
		return bookingClient.getBookings(userId, stateParam, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getOwnerBookings(@RequestHeader(CONST_SHARED_USER_ID) long ownerId,
												   @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
												   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
												   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		log.debug("Получен запрос на получение всех бронирований владельца с id={} со статусом {}, с параметрами from={}, size={}", ownerId, stateParam, from, size);
		return bookingClient.getOwnerBookings(ownerId, stateParam, from, size);
	}
}
