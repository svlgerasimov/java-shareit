package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingSearchState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody @Valid BookingDtoIn bookingDto,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingClient.add(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@PathVariable long bookingId,
                        @RequestParam boolean approved,
                        @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingClient.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@PathVariable long bookingId,
                                  @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingClient.findById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findByBooker(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        return bookingClient.findByBooker(userId, BookingSearchState.of(state), from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findByOwner(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        return bookingClient.findByOwner(userId, BookingSearchState.of(state), from, size);
    }
}
