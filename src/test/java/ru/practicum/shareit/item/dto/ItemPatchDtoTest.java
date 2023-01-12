package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemPatchDtoTest {
    @Autowired
    JacksonTester<ItemPatchDto> jacksonTester;

    private final ItemPatchDto itemPatchDto = new ItemPatchDto("name", "description", true);

    @Test
    void itemPatchDtoSerializationTest() throws IOException {
        assertThat(jacksonTester.write(itemPatchDto))
                .isEqualToJson("ItemPatchDto.json", JSONCompareMode.STRICT);
    }

    @Test
    void itemPatchDtoDeserializationTest() throws IOException {
        assertThat(jacksonTester.read("ItemPatchDto.json"))
                .usingRecursiveComparison()
                .isEqualTo(itemPatchDto);
    }
}