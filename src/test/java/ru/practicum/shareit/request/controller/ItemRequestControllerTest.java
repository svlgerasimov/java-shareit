package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutExtended;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private TestItemRequestBuilder itemRequestBuilder;

    @NoArgsConstructor(staticName = "defaultBuilder")
    @AllArgsConstructor(staticName = "all")
    @Setter
    @Getter
    @Accessors(chain = true, fluent = true)
    private static class TestItemRequestBuilder {
        private Long id = 1L;
        private String description = "description1";
        private LocalDateTime created = LocalDateTime.of(2001, 1, 1, 1, 1);
        private Long itemId = 11L;
        private String itemName = "itemName1";
        private String itemDescription = "itemDescription1";
        private Boolean itemAvailablae = true;
        private Long itemRequestId = 21L;

        public ItemRequestDtoIn buildDtoId() {
            return new ItemRequestDtoIn(description);
        }

        public ItemRequestDtoOut buildDtoOut() {
            return new ItemRequestDtoOut(id, description, created);
        }

        public ItemRequestDtoOutExtended buildDtoOutExtended() {
            return new ItemRequestDtoOutExtended(id, description, created,
                    List.of(new ItemDto(itemId, itemName, itemDescription, itemAvailablae, itemRequestId)));
        }
    }

    @BeforeEach
    void setUp() {
        itemRequestBuilder = TestItemRequestBuilder.defaultBuilder();
    }

    @Test
    void addWithValidDtoAndThenStatusOkAndJsonBody() throws Exception {
        long userId = 1001L;
        ItemRequestDtoIn inputDto = itemRequestBuilder.buildDtoId();
        ItemRequestDtoOut expectedDto = itemRequestBuilder.buildDtoOut();

        when(itemRequestService.add(inputDto, userId))
                .thenReturn(expectedDto);

        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(inputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedDto)));
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

        verify(itemRequestService, never()).add(any(), anyLong());
    }

    @Test
    void findByUserAndThenStatusOkAndJsonArrayBody() throws Exception {
        long userId = 1001L;
        ItemRequestDtoOutExtended expectedDto = itemRequestBuilder.buildDtoOutExtended();

        when(itemRequestService.findByRequestor(userId))
                .thenReturn(List.of(expectedDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(expectedDto))));
    }

    @Test
    void findAllByOtherUsersWithPaginationAndThenStatusOkAndJsonArrayBody() throws Exception {
        long userId = 1001L;
        long from = 10L;
        Integer size = 100;
        ItemRequestDtoOutExtended expectedDto = itemRequestBuilder.buildDtoOutExtended();

        when(itemRequestService.findByOtherUsers(userId, from, size))
                .thenReturn(List.of(expectedDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(expectedDto))));
    }

    @Test
    void findAllByOtherUsersWithoutPaginationAndThenStatusOkAndJsonArrayBody() throws Exception {
        long userId = 1001L;
        ItemRequestDtoOutExtended expectedDto = itemRequestBuilder.buildDtoOutExtended();

        when(itemRequestService.findByOtherUsers(userId, 0, null))
                .thenReturn(List.of(expectedDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(expectedDto))));
    }

    @Test
    void findAllByOtherUsersWithNegativeFromAndThenStatusBadRequest() throws Exception {
        long userId = 1001L;

        mvc.perform(get("/requests/all")
                        .param("from", "-1")
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).findByOtherUsers(anyLong(), anyLong(), any());
    }

    @Test
    void findAllByOtherUsersWithNegativeSizeAndThenStatusBadRequest() throws Exception {
        long userId = 1001L;

        mvc.perform(get("/requests/all")
                        .param("size", "-1")
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).findByOtherUsers(anyLong(), anyLong(), any());
    }

    @Test
    void findAllByOtherUsersWithZeroSizeAndThenStatusBadRequest() throws Exception {
        long userId = 1001L;

        mvc.perform(get("/requests/all")
                        .param("size", "0")
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).findByOtherUsers(anyLong(), anyLong(), any());
    }

    @Test
    void findByIdAndThenStatusOkAndJsonBody() throws Exception {
        long userId = 1001L;
        long requestId = itemRequestBuilder.id();
        ItemRequestDtoOutExtended expectedDto = itemRequestBuilder.buildDtoOutExtended();

        when(itemRequestService.findById(requestId, userId))
                .thenReturn(expectedDto);

        mvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedDto)));
    }
}