package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoInTest {
    @Autowired
    JacksonTester<CommentDtoIn> jacksonTester;

    private final CommentDtoIn commentDtoIn = new CommentDtoIn("text");
    private final String json = "{\"text\": \"text\"}";

    @Test
    void commentDtoInSerializationTest() throws IOException {
        assertThat(jacksonTester.write(commentDtoIn)).isEqualToJson(json, JSONCompareMode.STRICT);
    }

    @Test
    void commentDtoInDeserializationTest() throws IOException {
        assertThat(jacksonTester.parse(json)).usingRecursiveComparison().isEqualTo(commentDtoIn);
    }
}