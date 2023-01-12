package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {
    @Autowired
    JacksonTester<UserDto> jacksonTester;

    private final UserDto userDto = new UserDto(1L, "name", "user@mail.com");

    @Test
    void userDtoSerializationTest() throws IOException {
        assertThat(jacksonTester.write(userDto))
                .isEqualToJson("UserDto.json", JSONCompareMode.STRICT);
    }

    @Test
    void userDtoDeserializationTest() throws IOException {
        assertThat(jacksonTester.read("UserDto.json"))
                .usingRecursiveComparison()
                .isEqualTo(userDto);
    }
}