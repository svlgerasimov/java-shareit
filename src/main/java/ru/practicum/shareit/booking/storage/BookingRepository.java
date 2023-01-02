package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByIdAndItemOwnerId(Long id, Long ownerId);

    @Query(value = "select b " +
            "from Booking as b " +
            "join b.item as item " +
            "join item.owner as owner " +
            "join b.booker as booker " +
            "where b.id = :bookingId and (owner.id = :userId or booker.id = :userId)")
    Optional<Booking> findByIdAndItemOwnerIdOrBookerId(@Param("bookingId") Long id, @Param("userId") Long userId);

    List<Booking> findByBooker(User booker, Sort sort);

    List<Booking> findByBooker(User booker, Pageable pageable);

    List<Booking> findByBookerAndStartIsBeforeAndEndIsAfter(
            User booker, LocalDateTime maxStart, LocalDateTime minEnd, Sort sort);

    List<Booking> findByBookerAndStartIsBeforeAndEndIsAfter(
            User booker, LocalDateTime maxStart, LocalDateTime minEnd, Pageable pageable);

    List<Booking> findByBookerAndStartIsAfter(User booker, LocalDateTime minStart, Sort sort);

    List<Booking> findByBookerAndStartIsAfter(User booker, LocalDateTime minStart, Pageable pageable);

    List<Booking> findByBookerAndEndIsBefore(User booker, LocalDateTime maxEnd, Sort sort);

    List<Booking> findByBookerAndEndIsBefore(User booker, LocalDateTime maxEnd, Pageable pageable);

    List<Booking> findByBookerAndStatusIs(User booker, BookingStatus status, Sort sort);

    List<Booking> findByBookerAndStatusIs(User booker, BookingStatus status, Pageable pageable);

    List<Booking> findByItemOwner(User booker, Sort sort);

    List<Booking> findByItemOwner(User booker, Pageable pageable);

    List<Booking> findByItemOwnerAndStartIsBeforeAndEndIsAfter(
            User booker, LocalDateTime maxStart, LocalDateTime minEnd, Sort sort);

    List<Booking> findByItemOwnerAndStartIsBeforeAndEndIsAfter(
            User booker, LocalDateTime maxStart, LocalDateTime minEnd, Pageable pageable);

    List<Booking> findByItemOwnerAndStartIsAfter(User booker, LocalDateTime minStart, Sort sort);

    List<Booking> findByItemOwnerAndStartIsAfter(User booker, LocalDateTime minStart, Pageable pageable);

    List<Booking> findByItemOwnerAndEndIsBefore(User booker, LocalDateTime maxEnd, Sort sort);

    List<Booking> findByItemOwnerAndEndIsBefore(User booker, LocalDateTime maxEnd, Pageable pageable);

    List<Booking> findByItemOwnerAndStatusIs(User booker, BookingStatus status, Sort sort);

    List<Booking> findByItemOwnerAndStatusIs(User booker, BookingStatus status, Pageable pageable);

    Optional<Booking> findFirstByItemAndStartAfterAndStatusIs(
            Item item, LocalDateTime minStart, BookingStatus status, Sort sort);

    List<Booking> findAllByItemInAndStartAfterAndStatusIs(
            List<Item> items, LocalDateTime minStart, BookingStatus status, Sort sort);

    Optional<Booking> findFirstByItemAndStartLessThanEqualAndStatusIs(
            Item item, LocalDateTime maxStart, BookingStatus status, Sort sort);

    List<Booking> findAllByItemInAndStartLessThanEqualAndStatusIs(
            List<Item> items, LocalDateTime maxStart, BookingStatus status, Sort sort);

    Optional<Booking> findFirstByItemAndBookerAndEndBefore(Item item, User booker, LocalDateTime maxEnd);
}
