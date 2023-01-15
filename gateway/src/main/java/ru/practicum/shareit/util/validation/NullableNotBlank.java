package ru.practicum.shareit.util.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NullableNotBlankValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface NullableNotBlank {
    String message() default "Value is blank";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
