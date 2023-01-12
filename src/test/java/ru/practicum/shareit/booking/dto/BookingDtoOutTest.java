package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoOutTest {
    @Autowired
    JacksonTester<BookingDtoOut> jacksonTester;

    private final String json = "{\n" +
            "  \"id\": 1,\n" +
            "  \"start\": \"2001-02-03T04:05:00\",\n" +
            "  \"end\": \"2011-12-13T14:15:00\",\n" +
            "  \"item\": {\n" +
            "    \"id\":2,\n" +
            "    \"name\": \"itemName\",\n" +
            "    \"description\": \"itemDescription\",\n" +
            "    \"available\": true,\n" +
            "    \"requestId\": 3\n" +
            "  },\n" +
            "  \"booker\": {\n" +
            "    \"id\": 4,\n" +
            "    \"name\": \"userName\",\n" +
            "    \"email\": \"user@mail.com\"\n" +
            "  },\n" +
            "  \"status\": \"WAITING\"\n" +
            "}";

    private final BookingDtoOut bookingDtoOut = new BookingDtoOut(1L,
            LocalDateTime.of(2001, 2, 3, 4, 5),
            LocalDateTime.of(2011, 12, 13, 14, 15),
            new ItemDto(2L, "itemName", "itemDescription", true, 3L),
            new UserDto(4L, "userName", "user@mail.com"),
            BookingStatus.WAITING);

    @Test
    void bookingDtoOutSerializationTest() throws IOException {
        assertThat(jacksonTester.write(bookingDtoOut))
                .isEqualToJson(json, JSONCompareMode.STRICT);
    }

    @Test
    void bookingDtoOutDeserializationTest() throws IOException {
        assertThat(jacksonTester.parse(json))
                .usingRecursiveComparison()
                .isEqualTo(bookingDtoOut);
    }
}