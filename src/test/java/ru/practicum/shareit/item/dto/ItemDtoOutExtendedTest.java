package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoOutExtendedTest {
    @Autowired
    JacksonTester<ItemDtoOutExtended> jacksonTester;

    private final ItemDtoOutExtended itemDtoOutExtended = new ItemDtoOutExtended(
            1L, "name", "description", true, 2L,
            new BookingDtoShort(3L,
                    LocalDateTime.of(2001, 2, 3, 4, 5),
                    LocalDateTime.of(2011, 12, 13, 14, 15),
                    4L, BookingStatus.APPROVED),
            new BookingDtoShort(5L,
                    LocalDateTime.of(2002, 1, 4, 5, 6),
                    LocalDateTime.of(2012, 11, 14, 15, 16),
                    6L, BookingStatus.WAITING),
            List.of(
                    new CommentDtoOut(11L, "text1", "Author1",
                            LocalDateTime.of(2003, 4, 5, 6, 7)),
                    new CommentDtoOut(12L, "text2", "Author2",
                            LocalDateTime.of(2004, 5, 6, 7, 8)),
                    new CommentDtoOut(13L, "text3", "Author3",
                            LocalDateTime.of(2005, 6,7, 8, 9))
            )
    );

    private final String json = "{\n" +
            "  \"id\": 1,\n" +
            "  \"name\": \"name\",\n" +
            "  \"description\": \"description\",\n" +
            "  \"available\": true,\n" +
            "  \"requestId\": 2,\n" +
            "  \"lastBooking\": {\n" +
            "    \"id\": 3,\n" +
            "    \"start\": \"2001-02-03T04:05:00\",\n" +
            "    \"end\": \"2011-12-13T14:15:00\",\n" +
            "    \"bookerId\": 4,\n" +
            "    \"status\": \"APPROVED\"\n" +
            "  },\n" +
            "  \"nextBooking\": {\n" +
            "    \"id\": 5,\n" +
            "    \"start\": \"2002-01-04T05:06:00\",\n" +
            "    \"end\": \"2012-11-14T15:16:00\",\n" +
            "    \"bookerId\": 6,\n" +
            "    \"status\": \"WAITING\"\n" +
            "  },\n" +
            "  \"comments\": [\n" +
            "    {\n" +
            "      \"id\": 11,\n" +
            "      \"text\": \"text1\",\n" +
            "      \"authorName\": \"Author1\",\n" +
            "      \"created\": \"2003-04-05T06:07:00\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 12,\n" +
            "      \"text\": \"text2\",\n" +
            "      \"authorName\": \"Author2\",\n" +
            "      \"created\": \"2004-05-06T07:08:00\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 13,\n" +
            "      \"text\": \"text3\",\n" +
            "      \"authorName\": \"Author3\",\n" +
            "      \"created\": \"2005-06-07T08:09:00\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    @Test
    void itemDtoOutExtendedSerializationTest() throws IOException {
        assertThat(jacksonTester.write(itemDtoOutExtended))
                .isEqualToJson(json, JSONCompareMode.STRICT);
    }

    @Test
    void itemDtoOutExtendedDeserializationTest() throws IOException {
        assertThat(jacksonTester.parse(json))
                .usingRecursiveComparison()
                .isEqualTo(itemDtoOutExtended);
    }
}