package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoOutExtendedTest {
    @Autowired
    JacksonTester<ItemRequestDtoOutExtended> jacksonTester;

    private final ItemRequestDtoOutExtended itemRequestDtoOutExtended = new ItemRequestDtoOutExtended(1L,
            "description",
            LocalDateTime.of(2001, 2, 3, 4, 5),
            List.of(new ItemDto(2L, "itemName1", "itemDescription1", true, 3L),
                    new ItemDto(4L, "itemName2", "itemDescription2", false, 5L)));

    @Test
    void itemRequestDtoOutExtendedSerializationTest() throws IOException {
        assertThat(jacksonTester.write(itemRequestDtoOutExtended))
                .isEqualToJson("ItemRequestDtoOutExtended.json", JSONCompareMode.STRICT);
    }

    @Test
    void itemRequestDtoOutExtendedDeserializationTest() throws IOException {
        assertThat(jacksonTester.read("ItemRequestDtoOutExtended.json"))
                .usingRecursiveComparison()
                .isEqualTo(itemRequestDtoOutExtended);
    }
}