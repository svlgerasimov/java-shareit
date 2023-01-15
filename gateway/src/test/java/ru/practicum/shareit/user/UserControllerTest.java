package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Autowired
    private MockMvc mvc;

    private TestUserBuilder userBuilder;

    @NoArgsConstructor(staticName = "defaultBuilder")
    @AllArgsConstructor(staticName = "all")
    @Setter
    @Accessors(chain = true, fluent = true)
    private static class TestUserBuilder {
        private Long id = 1L;
        private String name = "name1";
        private String email = "email1@mail.com";

        public UserDto buildDto() {
            return new UserDto(id, name, email);
        }
    }

    @BeforeEach
    void setUp() {
        userBuilder = TestUserBuilder.defaultBuilder();
    }

    @Test
    void addDtoWithBlankNameAndThenStatusBadRequest() throws Exception {
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userBuilder.name("").buildDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addDtoWithBlankEmailAndThenStatusBadRequest() throws Exception {
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userBuilder.email("").buildDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addDtoWithIncorrectEmailAndThenStatusBadRequest() throws Exception {
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userBuilder.email("mail").buildDto()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchWithBlankNameAndThenStatusBadRequest() throws Exception {
        mvc.perform(patch("/users/{id}", 1)
                        .content(objectMapper.writeValueAsString(new UserPatchDto("",  null)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchWithBlankEmailAndThenStatusBadRequest() throws Exception {
        mvc.perform(patch("/users/{id}", 1)
                        .content(objectMapper.writeValueAsString(new UserPatchDto(null,  "")))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchWitIncorrectEmailAndThenStatusBadRequest() throws Exception {
        mvc.perform(patch("/users/{id}", 1)
                        .content(objectMapper.writeValueAsString(new UserPatchDto(null,  "mail")))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}