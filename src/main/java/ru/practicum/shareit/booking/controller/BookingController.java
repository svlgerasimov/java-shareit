package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingSearchState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOut add(@RequestBody @Valid BookingDtoIn bookingDto,
                             @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.add(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut approve(@PathVariable long bookingId,
                        @RequestParam boolean approved,
                        @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut findById(@PathVariable long bookingId,
                                  @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoOut> findByBooker(
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.findByBooker(userId, BookingSearchState.of(state));
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> findByOwner(
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.findByOwner(userId, BookingSearchState.of(state));
    }
}
