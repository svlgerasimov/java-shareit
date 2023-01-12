package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoShortTest {
    @Autowired
    JacksonTester<BookingDtoShort> jacksonTester;

    private final BookingDtoShort bookingDtoShort = new BookingDtoShort(1L,
            LocalDateTime.of(2001, 2, 3, 4, 5),
            LocalDateTime.of(2011, 12, 13, 14, 15),
            2L, BookingStatus.WAITING);

    private final String json = "{\n" +
            "  \"id\": 1,\n" +
            "  \"start\": \"2001-02-03T04:05:00\",\n" +
            "  \"end\": \"2011-12-13T14:15:00\",\n" +
            "  \"bookerId\": 2,\n" +
            "  \"status\": \"WAITING\"\n" +
            "}";

    @Test
    void bookingDtoShortSerializationTest() throws IOException {
        assertThat(jacksonTester.write(bookingDtoShort))
                .isEqualToJson(json, JSONCompareMode.STRICT);
    }

    @Test
    void bookingDtoShortDeserializationTest() throws IOException {
        assertThat(jacksonTester.parse(json))
                .usingRecursiveComparison()
                .isEqualTo(bookingDtoShort);
    }
}