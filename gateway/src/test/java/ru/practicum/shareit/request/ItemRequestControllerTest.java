package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Autowired
    private MockMvc mvc;

    private TestItemRequestBuilder itemRequestBuilder;

    @NoArgsConstructor(staticName = "defaultBuilder")
    @AllArgsConstructor(staticName = "all")
    @Setter
    @Getter
    @Accessors(chain = true, fluent = true)
    private static class TestItemRequestBuilder {
        private String description = "description1";

        public ItemRequestDtoIn buildDtoId() {
            return new ItemRequestDtoIn(description);
        }
    }

    @BeforeEach
    void setUp() {
        itemRequestBuilder = TestItemRequestBuilder.defaultBuilder();
    }

    @Test
    void addWithBlankDescriptionAndThenStatusBadRequest() throws Exception {
        long userId = 1001L;
        itemRequestBuilder.description("   ");
        ItemRequestDtoIn inputDto = itemRequestBuilder.buildDtoId();

        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(inputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).add(any(), anyLong());
    }

    @Test
    void findAllByOtherUsersWithoutPaginationParamsAndThenDefaultParams() throws Exception {
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L));

        verify(itemRequestClient, times(1))
                .findByOtherUsers(1L, 0L, 10);
    }

    @Test
    void findAllByOtherUsersWithNegativeFromAndThenStatusBadRequest() throws Exception {
        long userId = 1001L;

        mvc.perform(get("/requests/all")
                        .param("from", "-1")
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).findByOtherUsers(anyLong(), anyLong(), anyInt());
    }

    @Test
    void findAllByOtherUsersWithNegativeSizeAndThenStatusBadRequest() throws Exception {
        long userId = 1001L;

        mvc.perform(get("/requests/all")
                        .param("size", "-1")
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).findByOtherUsers(anyLong(), anyLong(), anyInt());
    }

    @Test
    void findAllByOtherUsersWithZeroSizeAndThenStatusBadRequest() throws Exception {
        long userId = 1001L;

        mvc.perform(get("/requests/all")
                        .param("size", "0")
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).findByOtherUsers(anyLong(), anyLong(), anyInt());
    }
}