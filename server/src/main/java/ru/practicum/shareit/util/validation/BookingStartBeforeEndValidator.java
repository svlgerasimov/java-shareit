package ru.practicum.shareit.util.validation;

import ru.practicum.shareit.booking.dto.BookingDtoIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.util.Objects;

public class BookingStartBeforeEndValidator implements ConstraintValidator<BookingStartBeforeEnd, BookingDtoIn> {
    @Override
    public void initialize(BookingStartBeforeEnd constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(BookingDtoIn dto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = dto.getStart();
        LocalDateTime end = dto.getEnd();
        if (Objects.isNull(start) || Objects.isNull(end)) {
            return false;
        }
        return start.isBefore(end);
    }
}
