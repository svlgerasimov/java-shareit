package ru.practicum.shareit.util.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BookingStartBeforeEndValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
public @interface BookingStartBeforeEnd {
    String message() default "Start is not earlier than end";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
