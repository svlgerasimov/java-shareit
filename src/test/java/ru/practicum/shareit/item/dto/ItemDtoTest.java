package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {
    @Autowired
    JacksonTester<ItemDto> jacksonTester;

    private final ItemDto itemDto = new ItemDto(1L, "name", "description", true, 2L);

    @Test
    void itemDtoSerializationTest() throws IOException {
        assertThat(jacksonTester.write(itemDto)).isEqualToJson("ItemDto.json", JSONCompareMode.STRICT);
    }

    @Test
    void itemDtoDeserializationTest() throws IOException {
        assertThat(jacksonTester.read("ItemDto.json"))
                .usingRecursiveComparison()
                .isEqualTo(itemDto);
    }
}