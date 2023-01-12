package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoInTest {
    @Autowired
    JacksonTester<ItemRequestDtoIn> jacksonTester;

    private final ItemRequestDtoIn itemRequestDtoIn = new ItemRequestDtoIn("description");
    private final String json = "{\"description\": \"description\"}";

    @Test
    void itemRequestDtoInSerializationTest() throws IOException {
        assertThat(jacksonTester.write(itemRequestDtoIn)).isEqualToJson(json, JSONCompareMode.STRICT);
    }

    @Test
    void itemRequestDtoInDeserializationTest() throws IOException {
        assertThat(jacksonTester.parse(json)).usingRecursiveComparison().isEqualTo(itemRequestDtoIn);
    }
}