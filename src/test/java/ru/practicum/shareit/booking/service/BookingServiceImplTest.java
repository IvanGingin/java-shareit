package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Иван Иванов", "ivan.ivanov@example.com");
        owner = new User(2L, "Саша Александров", "sasha.alexandrov@example.com");
        item = new Item(1L, "Вещь", "Описание вещи", true, owner, null);
        booking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, user, BookingStatus.WAITING);
        bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
    }

    @Test
    void testAddBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingDto result = bookingService.addBooking(user.getId(), bookingDto);
        assertNotNull(result);
        assertEquals(bookingDto.getItemId(), result.getItemId());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testAddBooking_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> {
            bookingService.addBooking(user.getId(), bookingDto);
        });
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void testAddBooking_ItemNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> {
            bookingService.addBooking(user.getId(), bookingDto);
        });
        assertEquals("Вещь не найдена", exception.getMessage());
    }

    @Test
    void testAddBooking_ItemNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Exception exception = assertThrows(ValidationException.class, () -> {
            bookingService.addBooking(user.getId(), bookingDto);
        });
        assertEquals("Вещь недоступна для бронирования", exception.getMessage());
    }

    @Test
    void testAddBooking_OwnerBookingOwnItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Exception exception = assertThrows(NotFoundException.class, () -> {
            bookingService.addBooking(owner.getId(), bookingDto);
        });
        assertEquals("Нельзя забронировать свою вещь", exception.getMessage());
    }

    @Test
    void testAddBooking_StartAfterEnd() {
        bookingDto.setStart(LocalDateTime.now().plusDays(2));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Exception exception = assertThrows(ValidationException.class, () -> {
            bookingService.addBooking(user.getId(), bookingDto);
        });
        assertEquals("Дата начала бронирования должна быть раньше даты окончания", exception.getMessage());
    }

    @Test
    void testUpdateBookingStatus() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingDto result = bookingService.updateBookingStatus(booking.getId(), owner.getId(), true);
        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testUpdateBookingStatus_BookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> {
            bookingService.updateBookingStatus(booking.getId(), owner.getId(), true);
        });
        assertEquals("Бронирование не найдено", exception.getMessage());
    }

    @Test
    void testUpdateBookingStatus_UserNotOwner() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        Exception exception = assertThrows(NotFoundException.class, () -> {
            bookingService.updateBookingStatus(booking.getId(), user.getId(), true);
        });
        assertEquals("Пользователь не является владельцем вещи", exception.getMessage());
    }

    @Test
    void testUpdateBookingStatus_AlreadyApproved() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        Exception exception = assertThrows(ValidationException.class, () -> {
            bookingService.updateBookingStatus(booking.getId(), owner.getId(), true);
        });
        assertEquals("Бронирование уже подтверждено", exception.getMessage());
    }

    @Test
    void testGetBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        BookingDto result = bookingService.getBooking(booking.getId(), user.getId());
        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void testGetBooking_BookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> {
            bookingService.getBooking(booking.getId(), user.getId());
        });
        assertEquals("Бронирование не найдено", exception.getMessage());
    }

    @Test
    void testGetBooking_UserNotAuthorized() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        Exception exception = assertThrows(NotFoundException.class, () -> {
            bookingService.getBooking(booking.getId(), owner.getId() + 1);
        });
        assertEquals("Пользователь не имеет доступа к этому бронированию", exception.getMessage());
    }

    @Test
    void testGetUserBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDto> result = bookingService.getUserBookings(user.getId(), "ALL", 0, 10);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void testGetUserBookings_UnsupportedState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Exception exception = assertThrows(UnsupportedException.class, () -> {
            bookingService.getUserBookings(user.getId(), "UNSUPPORTED", 0, 10);
        });
        assertEquals("{\"error\":\"Unknown state: UNSUPPORTED\"}", exception.getMessage());
    }

    @Test
    void testGetOwnerBookings() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), "ALL", 0, 10);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void testGetOwnerBookings_UnsupportedState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        Exception exception = assertThrows(UnsupportedException.class, () -> {
            bookingService.getOwnerBookings(owner.getId(), "UNSUPPORTED", 0, 10);
        });
        assertEquals("{\"error\":\"Unknown state: UNSUPPORTED\"}", exception.getMessage());
    }

    @Test
    void testGetUserBookings_Current() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findCurrentBookings(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> result = bookingService.getUserBookings(user.getId(), "CURRENT", 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void testGetUserBookings_Past() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findPastBookings(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> result = bookingService.getUserBookings(user.getId(), "PAST", 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void testGetUserBookings_Future() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findFutureBookings(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> result = bookingService.getUserBookings(user.getId(), "FUTURE", 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void testGetUserBookings_Waiting() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStatus(anyLong(), eq(BookingStatus.WAITING), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> result = bookingService.getUserBookings(user.getId(), "WAITING", 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void testGetUserBookings_Rejected() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStatus(anyLong(), eq(BookingStatus.REJECTED), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> result = bookingService.getUserBookings(user.getId(), "REJECTED", 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void testGetOwnerBookings_Current() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findCurrentOwnerBookings(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), "CURRENT", 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void testGetOwnerBookings_Past() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findPastOwnerBookings(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), "PAST", 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void testGetOwnerBookings_Future() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findFutureOwnerBookings(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), "FUTURE", 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void testGetOwnerBookings_Waiting() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findOwnerBookingsByStatus(anyLong(), eq(BookingStatus.WAITING), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), "WAITING", 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void testGetOwnerBookings_Rejected() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findOwnerBookingsByStatus(anyLong(), eq(BookingStatus.REJECTED), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> result = bookingService.getOwnerBookings(owner.getId(), "REJECTED", 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(booking.getId(), result.get(0).getId());
    }
}
