package ru.practicum.shareit.booking.dto;

import lombok.Value;
import ru.practicum.shareit.util.validation.BookingStartBeforeEnd;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
@BookingStartBeforeEnd
public class BookingDtoIn {

    @NotNull
    @FutureOrPresent
    LocalDateTime start;

    @NotNull
    @Future
    LocalDateTime end;

    @NotNull
    Long itemId;
}
