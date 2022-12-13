package ru.practicum.shareit.booking.storage;

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

    List<Booking> findByBookerOrderByStartDesc(User booker);

    List<Booking> findByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            User booker, LocalDateTime maxStart, LocalDateTime minEnd);

    List<Booking> findByBookerAndStartIsAfterOrderByStartDesc(User booker, LocalDateTime minStart);

    List<Booking> findByBookerAndEndIsBeforeOrderByStartDesc(User booker, LocalDateTime maxEnd);

    List<Booking> findByBookerAndStatusIsOrderByStartDesc(User booker, BookingStatus status);

    List<Booking> findByItemOwnerOrderByStartDesc(User booker);

    List<Booking> findByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            User booker, LocalDateTime maxStart, LocalDateTime minEnd);

    List<Booking> findByItemOwnerAndStartIsAfterOrderByStartDesc(User booker, LocalDateTime minStart);

    List<Booking> findByItemOwnerAndEndIsBeforeOrderByStartDesc(User booker, LocalDateTime maxEnd);

    List<Booking> findByItemOwnerAndStatusIsOrderByStartDesc(User booker, BookingStatus status);

    Optional<Booking> findFirstByItemAndStartAfterOrderByStartDesc(Item item, LocalDateTime minStart);

    Optional<Booking> findFirstByItemAndStartBeforeOrderByStartDesc(Item item, LocalDateTime maxStart);

    Optional<Booking> findFirstByItemAndBookerAndEndBefore(Item item, User booker, LocalDateTime maxEnd);
}
