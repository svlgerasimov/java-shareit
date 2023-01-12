package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoOutTest {
    @Autowired
    JacksonTester<ItemRequestDtoOut> jacksonTester;

    private final ItemRequestDtoOut itemRequestDtoOut = new ItemRequestDtoOut(1L,
            "description",
            LocalDateTime.of(2001, 2, 3, 4, 5));

    @Test
    void itemRequestDtoOutSerializationTest() throws IOException {
        assertThat(jacksonTester.write(itemRequestDtoOut))
                .isEqualToJson("ItemRequestDtoOut.json", JSONCompareMode.STRICT);
    }

    @Test
    void itemRequestDtoOutDeserializationTest() throws IOException {
        assertThat(jacksonTester.read("ItemRequestDtoOut.json"))
                .usingRecursiveComparison()
                .isEqualTo(itemRequestDtoOut);
    }
}