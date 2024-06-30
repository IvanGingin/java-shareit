package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId ORDER BY b.start DESC")
    Page<Booking> findByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId ORDER BY b.start DESC")
    Page<Booking> findByItemOwnerId(Long ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start < :currentTime AND b.end > :currentTime")
    Page<Booking> findCurrentBookings(Long userId, LocalDateTime currentTime, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start < :currentTime AND b.end > :currentTime")
    Page<Booking> findCurrentOwnerBookings(Long ownerId, LocalDateTime currentTime, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.end < :currentTime ORDER BY b.start DESC")
    Page<Booking> findPastBookings(Long userId, LocalDateTime currentTime, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.end < :currentTime ORDER BY b.start DESC")
    Page<Booking> findPastOwnerBookings(Long ownerId, LocalDateTime currentTime, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start > :currentTime ORDER BY b.start DESC")
    Page<Booking> findFutureBookings(Long userId, LocalDateTime currentTime, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start > :currentTime ORDER BY b.start DESC")
    Page<Booking> findFutureOwnerBookings(Long ownerId, LocalDateTime currentTime, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.status = :status ORDER BY b.start DESC")
    Page<Booking> findByBookerIdAndStatus(Long userId, BookingStatus status, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.status = :status ORDER BY b.start DESC")
    Page<Booking> findOwnerBookingsByStatus(Long ownerId, BookingStatus status, Pageable pageable);

    boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long bookerId, BookingStatus status, LocalDateTime end);

    Booking findFirstByItemIdAndStartLessThanEqualAndStatusOrderByEndDesc(Long itemId, LocalDateTime start, BookingStatus status);

    Booking findFirstByItemIdAndStartGreaterThanEqualAndStatusOrderByStartAsc(Long itemId, LocalDateTime start, BookingStatus status);
}
