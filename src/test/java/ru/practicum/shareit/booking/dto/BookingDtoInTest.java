package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoInTest {
    @Autowired
    JacksonTester<BookingDtoIn> jacksonTester;

    private final BookingDtoIn bookingDtoIn = new BookingDtoIn(
            LocalDateTime.of(2001, 2, 3, 4, 5),
            LocalDateTime.of(2011, 12, 13, 14, 15),
            1L
    );

    @Test
    void bookingDtoInSerializationTest() throws IOException {
        assertThat(jacksonTester.write(bookingDtoIn)).isEqualToJson("bookingDtoIn.json");
    }

    @Test
    void bookingDtoInDeserializationTest() throws IOException {
        assertThat(jacksonTester.read("bookingDtoIn.json"))
                .usingRecursiveComparison()
                .isEqualTo(bookingDtoIn);
    }
}