package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId ORDER BY b.start DESC")
    List<Booking> findByBookerIdOrderByStartDesc(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId ORDER BY b.start DESC")
    List<Booking> findByItemOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start < :currentTime AND b.end > :currentTime")
    List<Booking> findCurrentBookings(Long userId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start < :currentTime AND b.end > :currentTime")
    List<Booking> findCurrentOwnerBookings(Long ownerId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.end < :currentTime ORDER BY b.start DESC")
    List<Booking> findPastBookings(Long userId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.end < :currentTime ORDER BY b.start DESC")
    List<Booking> findPastOwnerBookings(Long ownerId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start > :currentTime ORDER BY b.start DESC")
    List<Booking> findFutureBookings(Long userId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start > :currentTime ORDER BY b.start DESC")
    List<Booking> findFutureOwnerBookings(Long ownerId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findByBookerIdAndStatus(Long userId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findOwnerBookingsByStatus(Long ownerId, BookingStatus status);

    boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long bookerId, BookingStatus status, LocalDateTime end);

    Booking findFirstByItemIdAndStartLessThanEqualAndStatusOrderByEndDesc(Long itemId, LocalDateTime start, BookingStatus status);

    Booking findFirstByItemIdAndStartGreaterThanEqualAndStatusOrderByStartAsc(Long itemId, LocalDateTime start, BookingStatus status);
}
