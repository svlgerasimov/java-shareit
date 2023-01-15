package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

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
        private Long lastBookingId = 21L;
        private LocalDateTime lastBookingStart = LocalDateTime.now().minusDays(4);
        private LocalDateTime lastBookingEnd = LocalDateTime.now().minusDays(3);
        private Long lastBookingBookerId = 31L;
        private BookingStatus lastBookingStatus = BookingStatus.APPROVED;
        private Long nextBookingId = 41L;
        private LocalDateTime nextBookingStart = LocalDateTime.now().plusDays(3);
        private LocalDateTime nextBookingEnd = LocalDateTime.now().plusDays(4);
        private Long nextBookingBookerId = 51L;
        private BookingStatus nextBookingStatus = BookingStatus.APPROVED;
        @Setter(AccessLevel.NONE)
        private TestCommentBuilder commentBuilder = TestCommentBuilder.defaultBuilder();

        public ItemDto buildDto() {
            return new ItemDto(id, name, description, available, requestId);
        }

        public ItemDtoOutExtended buildDtoExtended() {
            return new ItemDtoOutExtended(id, name, description, available, requestId,
                    new BookingDtoShort(lastBookingId, lastBookingStart, lastBookingEnd,
                            lastBookingBookerId, lastBookingStatus),
                    new BookingDtoShort(nextBookingId, nextBookingStart, nextBookingEnd,
                            nextBookingBookerId, nextBookingStatus),
                    List.of(commentBuilder.buildDtoOut()));
        }
    }

    @NoArgsConstructor(staticName = "defaultBuilder")
    @AllArgsConstructor(staticName = "all")
    @Setter
    @Getter
    @Accessors(chain = true, fluent = true)
    private static class TestCommentBuilder {
        private Long id = 101L;
        private String text = "commentText1";
        private String authorName = "commentAuthorName1";
        private LocalDateTime created = LocalDateTime.of(2001, 1, 1, 1, 1);

        public CommentDtoIn buildDtoIn() {
            return new CommentDtoIn(text);
        }

        public CommentDtoOut buildDtoOut() {
            return new CommentDtoOut(id, text, authorName, created);
        }
    }

    @BeforeEach
    void setUp() {
        itemBuilder = TestItemBuilder.defaultBuilder();
        commentBuilder = TestCommentBuilder.defaultBuilder();
    }

    @Test
    void getByIdAndThenStatusOkAndJsonBody() throws Exception {
        Long itemId = itemBuilder.id();
        long userId = 1001L;
        ItemDtoOutExtended expectedDto = itemBuilder.buildDtoExtended();

        when(itemService.getById(itemId, userId))
                .thenReturn(expectedDto);

        mvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedDto)));
    }

    @Test
    void getByIdWithoutUserHeaderAndThenStatusBadRequest() throws Exception {
        mvc.perform(get("/items/{id}", itemBuilder.id()))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getById(anyLong(), anyLong());
    }

    @Test
    void getAllWithPaginationParamsAndThenStatusOkAndJsonArrayBody() throws Exception {
        long userId = 1001L;
        ItemDtoOutExtended expectedDto = itemBuilder.buildDtoExtended();
        long from = 10;
        Integer size = 100;

        when(itemService.getAll(userId, from, size))
                .thenReturn(List.of(expectedDto));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(expectedDto))));
    }

    @Test
    void searchWithPaginationAndThenStatusOkAndJsonBody() throws Exception {
        String text = "text";
        long from = 10L;
        Integer size = 100;

        when(itemService.search(text, from, size))
                .thenReturn(List.of(itemBuilder.buildDto()));

        mvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemBuilder.buildDto()))));
    }

    @Test
    void addValidDtoAndThenStatusOkAndJsonBody() throws Exception {
        long userId = 1001L;
        ItemDto expectedDto = itemBuilder.buildDto();
        itemBuilder.id(null);
        ItemDto inputDto = itemBuilder.buildDto();

        when(itemService.add(inputDto, userId))
                .thenReturn(expectedDto);

        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(inputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedDto)));
    }

    @Test
    void patchWithAvailableAndThenStatusOkAndJsonBody() throws Exception {
        itemBuilder.available(true);
        long itemId = itemBuilder.id();
        long userId = 1001L;
        ItemDto expectedDto = itemBuilder.buildDto();
        ItemPatchDto patchDto = new ItemPatchDto(null, null, itemBuilder.available());

        when(itemService.patch(itemId, patchDto, userId))
                .thenReturn(expectedDto);

        mvc.perform(patch("/items/{id}", itemId)
                        .content(objectMapper.writeValueAsString(patchDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedDto)));
    }

    @Test
    void patchWithValidNameAndThenStatusOkAndJsonBody() throws Exception {
        itemBuilder.name("name");
        long itemId = itemBuilder.id();
        long userId = 1001L;
        ItemDto expectedDto = itemBuilder.buildDto();
        ItemPatchDto patchDto = new ItemPatchDto(itemBuilder.name(), null, null);

        when(itemService.patch(itemId, patchDto, userId))
                .thenReturn(expectedDto);

        mvc.perform(patch("/items/{id}", itemId)
                        .content(objectMapper.writeValueAsString(patchDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedDto)));

    }

    @Test
    void patchWithValidDescriptionAndThenStatusOkAndJsonBody() throws Exception {
        itemBuilder.description("description");
        long itemId = itemBuilder.id();
        long userId = 1001L;
        ItemDto expectedDto = itemBuilder.buildDto();
        ItemPatchDto patchDto = new ItemPatchDto(null, itemBuilder.description(), null);

        when(itemService.patch(itemId, patchDto, userId))
                .thenReturn(expectedDto);

        mvc.perform(patch("/items/{id}", itemId)
                        .content(objectMapper.writeValueAsString(patchDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedDto)));

    }

    @Test
    void addCommentWithValidDtoAndThenStatusOkAndJsonBody() throws Exception {
        CommentDtoIn inputDto = commentBuilder.buildDtoIn();
        CommentDtoOut expectedDto = commentBuilder.buildDtoOut();
        long itemId = 1001L;
        long userId = 1011L;

        when(itemService.addComment(inputDto, itemId, userId))
                .thenReturn(expectedDto);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(objectMapper.writeValueAsString(inputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedDto)));
    }
}