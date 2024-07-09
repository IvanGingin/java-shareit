package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void testFindByBookerIdOrderByStartDesc() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("user@test.com");
        em.persist(user);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);
        em.persist(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);

        List<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDesc(user.getId(), Pageable.unpaged()).getContent();
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void testFindByItemOwnerId() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@test.com");
        em.persist(owner);

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@test.com");
        em.persist(booker);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);

        List<Booking> bookings = bookingRepository.findByItemOwnerId(owner.getId(), Pageable.unpaged()).getContent();
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void testFindCurrentBookings() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("user@test.com");
        em.persist(user);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);
        em.persist(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);

        List<Booking> bookings = bookingRepository.findCurrentBookings(user.getId(), LocalDateTime.now(), Pageable.unpaged()).getContent();
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void testFindPastBookings() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("user@test.com");
        em.persist(user);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);
        em.persist(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);

        List<Booking> bookings = bookingRepository.findPastBookings(user.getId(), LocalDateTime.now(), Pageable.unpaged()).getContent();
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void testFindFutureBookings() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("user@test.com");
        em.persist(user);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);
        em.persist(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);

        List<Booking> bookings = bookingRepository.findFutureBookings(user.getId(), LocalDateTime.now(), Pageable.unpaged()).getContent();
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void testFindByBookerIdAndStatus() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("user@test.com");
        em.persist(user);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);
        em.persist(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);

        List<Booking> bookings = bookingRepository.findByBookerIdAndStatus(user.getId(), BookingStatus.WAITING, Pageable.unpaged()).getContent();
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void testFindOwnerBookingsByStatus() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@test.com");
        em.persist(owner);

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@test.com");
        em.persist(booker);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);

        List<Booking> bookings = bookingRepository.findOwnerBookingsByStatus(owner.getId(), BookingStatus.WAITING, Pageable.unpaged()).getContent();
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void testFindPastOwnerBookings() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@test.com");
        em.persist(owner);

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@test.com");
        em.persist(booker);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);

        List<Booking> bookings = bookingRepository.findPastOwnerBookings(owner.getId(), LocalDateTime.now(), Pageable.unpaged()).getContent();
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void testFindCurrentOwnerBookings() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@test.com");
        em.persist(owner);

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@test.com");
        em.persist(booker);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);

        List<Booking> bookings = bookingRepository.findCurrentOwnerBookings(owner.getId(), LocalDateTime.now(), Pageable.unpaged()).getContent();
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void testFindFutureOwnerBookings() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@test.com");
        em.persist(owner);

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@test.com");
        em.persist(booker);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);

        List<Booking> bookings = bookingRepository.findFutureOwnerBookings(owner.getId(), LocalDateTime.now(), Pageable.unpaged()).getContent();
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(booking.getId(), bookings.get(0).getId());
    }
}
