package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserPatchDtoTest {
    @Autowired
    JacksonTester<UserPatchDto> jacksonTester;

    private final UserPatchDto userPatchDto = new UserPatchDto("name", "user@mail.com");

    @Test
    void userPatchDtoSerializationTest() throws IOException {
        assertThat(jacksonTester.write(userPatchDto))
                .isEqualToJson("UserPatchDto.json", JSONCompareMode.STRICT);
    }

    @Test
    void userPatchDtoDeserializationTest() throws IOException {
        assertThat(jacksonTester.read("UserPatchDto.json"))
                .usingRecursiveComparison()
                .isEqualTo(userPatchDto);
    }
}