package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private MockMvc mvc;

    private TestCommentBuilder commentBuilder;
    private TestItemBuilder itemBuilder;

    @NoArgsConstructor(staticName = "defaultBuilder")
    @AllArgsConstructor(staticName = "all")
    @Setter
    @Getter
    @Accessors(chain = true, fluent = true)
    private static class TestItemBuilder {
        private Long id = 1L;
        private String name = "name1";
        private String description = "description1";
        private Boolean available = true;
        private Long requestId = 11L;

        public ItemDto buildDto() {
            return new ItemDto(id, name, description, available, requestId);
        }
    }

    @NoArgsConstructor(staticName = "defaultBuilder")
    @AllArgsConstructor(staticName = "all")
    @Setter
    @Getter
    @Accessors(chain = true, fluent = true)
    private static class TestCommentBuilder {
        private String text = "commentText1";

        public CommentDtoIn buildDtoIn() {
            return new CommentDtoIn(text);
        }
    }

    @BeforeEach
    void setUp() {
        itemBuilder = TestItemBuilder.defaultBuilder();
        commentBuilder = TestCommentBuilder.defaultBuilder();
    }

    @Test
    void getAllWithoutPaginationParamsAndThenDefaultParams() throws Exception {
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L));

        verify(itemClient, times(1))
                .getAll(1L, 0L, 10);
    }

    @Test
    void getAllWithNegativeFromAndThenStatusBadRequest() throws Exception {
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", String.valueOf(1001L))
                        .param("from", "-1"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getAll(anyLong(), anyLong(), anyInt());
    }

    @Test
    void getAllWithNegativeSizeAndThenStatusBadRequest() throws Exception {
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", String.valueOf(1001L))
                        .param("size", "-1"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getAll(anyLong(), anyLong(), anyInt());
    }

    @Test
    void getAllWithZeroSizeAndThenStatusBadRequest() throws Exception {
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", String.valueOf(1001L))
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getAll(anyLong(), anyLong(), anyInt());
    }

    @Test
    void searchWithoutPaginationParamsAndThenDefaultParams() throws Exception {
        mvc.perform(get("/items/search")
                        .param("text", "text"));

        verify(itemClient, times(1))
                .search("text", 0L, 10);
    }

    @Test
    void searchWithBlankTextAndThenStatusOkAndEmptyJsonArrayBody() throws Exception {
        String text = "   ";

        mvc.perform(get("/items/search")
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.emptyList())));

        verify(itemClient, never()).search(any(), anyLong(), anyInt());
    }

    @Test
    void searchWithNegativeFromAndThenStatusBadRequest() throws Exception {
        mvc.perform(get("/items/search")
                        .param("text", "text")
                        .param("from", "-1"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).search(any(), anyLong(), anyInt());
    }

    @Test
    void searchWithNegativeSizeAndThenStatusBadRequest() throws Exception {
        mvc.perform(get("/items/search")
                        .param("text", "text")
                        .param("size", "-1"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).search(any(), anyLong(), anyInt());
    }

    @Test
    void searchWithZeroSizeAndThenStatusBadRequest() throws Exception {
        mvc.perform(get("/items/search")
                        .param("text", "text")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).search(any(), anyLong(), anyInt());
    }

    @Test
    void addDtoWithBlankNameAndThenStatusBadRequest() throws Exception {
        long userId = 1001L;
        itemBuilder.id(null).name("   ");
        ItemDto inputDto = itemBuilder.buildDto();

        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(inputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).add(any(), anyLong());
    }

    @Test
    void addDtoWithBlankDescriptionAndThenStatusBadRequest() throws Exception {
        long userId = 1001L;
        itemBuilder.id(null).description("   ");
        ItemDto inputDto = itemBuilder.buildDto();

        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(inputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).add(any(), anyLong());
    }

    @Test
    void addDtoWithNullAvailableAndThenStatusBadRequest() throws Exception {
        long userId = 1001L;
        itemBuilder.id(null).available(null);
        ItemDto inputDto = itemBuilder.buildDto();

        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(inputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).add(any(), anyLong());
    }

    @Test
    void patchWithBlankNameAndThenStatusOkAndJsonBody() throws Exception {
        itemBuilder.name("  ");
        long itemId = itemBuilder.id();
        long userId = 1001L;
        ItemPatchDto patchDto = new ItemPatchDto(itemBuilder.name(), null, null);

        mvc.perform(patch("/items/{id}", itemId)
                        .content(objectMapper.writeValueAsString(patchDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).patch(anyLong(), any(), anyLong());
    }

    @Test
    void patchWithBlankDescriptionAndThenStatusOkAndJsonBody() throws Exception {
        itemBuilder.description("  ");
        long itemId = itemBuilder.id();
        long userId = 1001L;
        ItemPatchDto patchDto = new ItemPatchDto(null, itemBuilder.description, null);

        mvc.perform(patch("/items/{id}", itemId)
                        .content(objectMapper.writeValueAsString(patchDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).patch(anyLong(), any(), anyLong());
    }

    @Test
    void addCommentWithBlankTextAndThenStatusBadRequest() throws Exception {
        commentBuilder.text("   ");
        CommentDtoIn inputDto = commentBuilder.buildDtoIn();
        long itemId = 1001L;
        long userId = 1011L;

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(objectMapper.writeValueAsString(inputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addComment(any(), anyLong(), anyLong());
    }
}