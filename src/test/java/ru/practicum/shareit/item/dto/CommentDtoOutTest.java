package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoOutTest {
    @Autowired
    JacksonTester<CommentDtoOut> jacksonTester;

    private final String json = "{\n" +
            "  \"id\": 1,\n" +
            "  \"text\": \"text\",\n" +
            "  \"authorName\": \"Author\",\n" +
            "  \"created\": \"2001-02-03T04:05:00\"\n" +
            "}";

    private final CommentDtoOut commentDtoOut = new CommentDtoOut(1L,
            "text", "Author",
            LocalDateTime.of(2001, 2, 3, 4, 5));

    @Test
    void commentDtoOutSerializationTest() throws IOException {
        assertThat(jacksonTester.write(commentDtoOut))
                .isEqualToJson(json, JSONCompareMode.STRICT);
    }

    @Test
    void commentDtoOutDeserializationTest() throws IOException {
        assertThat(jacksonTester.parse(json))
                .usingRecursiveComparison()
                .isEqualTo(commentDtoOut);
    }
}