package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoInTest {
    @Autowired
    JacksonTester<BookingDtoIn> jacksonTester;

    @Test
    void testBookingDtoInDeserialization() throws IOException {
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                LocalDateTime.of(2001, 2, 3, 4, 5),
                LocalDateTime.of(2011, 12, 13, 14, 15),
                1L
        );

        JsonContent<BookingDtoIn> result = jacksonTester.write(bookingDtoIn);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.start")
                .isEqualTo("2001-02-03T04:05:00");
        assertThat(result).extractingJsonPathValue("$.end")
                .isEqualTo("2011-12-13T14:15:00");
    }
}