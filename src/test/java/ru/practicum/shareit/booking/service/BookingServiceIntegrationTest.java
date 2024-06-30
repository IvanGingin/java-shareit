package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {

    private final EntityManager em;
    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Test
    void testAddBookingIntegration() {
        User owner = new User(null, "Иван Иванов", "ivan.ivanov@example.com");
        User booker = new User(null, "Саша Александров", "sasha.alexandrov@example.com");
        userRepository.save(owner);
        userRepository.save(booker);
        Item item = new Item(null, "Предмет", "Описание предмета", true, owner, null);
        itemRepository.save(item);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = new BookingDto(null, start, end, item.getId(), null, null, BookingStatus.WAITING);
        BookingDto savedBookingDto = bookingService.addBooking(booker.getId(), bookingDto);
        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking savedBooking = query.setParameter("id", savedBookingDto.getId()).getSingleResult();
        assertThat(savedBooking.getId(), notNullValue());
        assertThat(savedBooking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(savedBooking.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(savedBooking.getItem().getId(), equalTo(item.getId()));
        assertThat(savedBooking.getBooker().getId(), equalTo(booker.getId()));
        assertThat(savedBooking.getStatus(), equalTo(BookingStatus.WAITING));
    }
}
