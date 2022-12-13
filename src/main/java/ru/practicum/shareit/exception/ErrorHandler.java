package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(NotFoundException exception) {
        Map<String, String> result = Map.of("Not Found Error", exception.getMessage());
        log.warn(String.valueOf(result), exception);
        return result;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleCustomValidationException(CustomValidationException exception) {
        Map<String, String> result = Map.of("error", exception.getMessage());
        log.warn(String.valueOf(result), exception);
        return result;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public Map<String, String> handleNotImplementedException(NotImplementedException exception) {
        Map<String, String> result = Map.of("Function not implemented", exception.getMessage());
        log.warn(String.valueOf(result), exception);
        return result;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflictException(ConflictException exception) {
        Map<String, String> result = Map.of("Conflict Error", exception.getMessage());
        log.warn(String.valueOf(result), exception);
        return result;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleAuthenticationErrorException(AuthenticationErrorException exception) {
        Map<String, String> result = Map.of("Authentication Fail", exception.getMessage());
        log.warn(String.valueOf(result), exception);
        return result;
    }

    // Ошибка валидации полей десериализируемого объекта @Valid
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String, String> result = exception.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fieldError ->
                                "Validation Error in field '" + fieldError.getField() +
                                        "' with value = '" + fieldError.getRejectedValue() + "'",
                        fieldError -> Objects.requireNonNullElse(fieldError.getDefaultMessage(), "")));
        log.warn(String.valueOf(result), exception);
        return result;
    }

    // Ошибка валидации параметра запроса @Validated
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleConstraintViolationException(ConstraintViolationException exception) {
        Map<String, String> result = Map.of("Bad Request", exception.getMessage());
        log.warn(String.valueOf(result), exception);
        return result;
    }

    // HttpMessageNotReadableException выбрасывается например, если отсутствует тело запроса
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        String message = exception.getMessage();
        Map<String, String> result = Map.of("Bad Request", Objects.isNull(message) ? "Details unknown" : message);
        log.warn(String.valueOf(result), exception);
        return result;
    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public Map<String, String> handleMethodArgumentTypeMismatchException(
//            MethodArgumentTypeMismatchException exception) {
//        String message = exception.getMessage();
//        Map<String, String> result = Map.of("error", Objects.isNull(message) ? "Details unknown" : message);
//        log.warn(String.valueOf(result), exception);
//        return result;
//    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMissingRequestHeaderException(MissingRequestHeaderException exception) {
        String message = exception.getMessage();
        Map<String, String> result = Map.of("Bad Request", Objects.isNull(message) ? "Details unknown" : message);
        log.warn(String.valueOf(result), exception);
        return result;
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleUnexpectedException(Throwable exception) {
        Map<String, String> result = Map.of("Internal Server Error", exception.getMessage());
        log.warn(String.valueOf(result), exception);
        return result;
    }
}
