package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingSearchState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private TestBookingBuilder bookingBuilder;

    @NoArgsConstructor(staticName = "defaultBuilder")
    @AllArgsConstructor(staticName = "all")
    @Setter
    @Getter
    @Accessors(chain = true, fluent = true)
    private static class TestBookingBuilder {
        private Long id = 1L;
        private LocalDateTime start = LocalDateTime.now().plusDays(2);
        private LocalDateTime end = LocalDateTime.now().plusDays(4);
        private BookingStatus status = BookingStatus.WAITING;
        private Long itemId = 11L;
        private String itemName = "itemName1";
        private String itemDescription = "itemDescription1";
        private Boolean itemAvailable = true;
        private Long itemOwnerId = 21L;
        private Long bookerId = 31L;
        private String bookerName = "bookerName1";
        private String bookerEmail = "bookeremail1@mail.com";

        public BookingDtoIn buildDtoIn() {
            return new BookingDtoIn(start, end, itemId);
        }

        public BookingDtoOut buildDtoOut() {
            return new BookingDtoOut(
                    id,
                    start,
                    end,
                    new ItemDto(itemId, itemName, itemDescription, itemAvailable, id),
                    new UserDto(bookerId, bookerName, bookerEmail),
                    status
            );
        }
    }

    @BeforeEach
    void setUp() {
        bookingBuilder = TestBookingBuilder.defaultBuilder();
    }

    @Test
    void addValidDtoAndThenStatusOkAndJsonBody() throws Exception {
        BookingDtoIn inputDto = bookingBuilder.buildDtoIn();
        BookingDtoOut expectedDto = bookingBuilder.buildDtoOut();

        when(bookingService.add(inputDto, bookingBuilder.bookerId())).
                thenReturn(expectedDto);

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(inputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookingBuilder.bookerId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingBuilder.buildDtoOut())));
    }

    @Test
    void addDtoWithStartInPastAndThenStatusBadRequest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        bookingBuilder.start(now.minusDays(2)).end(now.plusDays(2));

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingBuilder.buildDtoIn()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookingBuilder.bookerId()))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).add(any(), anyLong());
    }

    @Test
    void addDtoWithStartAfterEndAndThenStatusBadRequest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        bookingBuilder.start(now.plusDays(4)).end(now.plusDays(2));

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingBuilder.buildDtoIn()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookingBuilder.bookerId()))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).add(any(), anyLong());
    }

    @Test
    void addDtoWithNullStartAndThenStatusBadRequest() throws Exception {
        bookingBuilder.start(null);

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingBuilder.buildDtoIn()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookingBuilder.bookerId()))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).add(any(), anyLong());
    }

    @Test
    void addDtoWithNullEndAndThenStatusBadRequest() throws Exception {
        bookingBuilder.end(null);

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingBuilder.buildDtoIn()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookingBuilder.bookerId()))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).add(any(), anyLong());
    }

    @Test
    void addDtoWithNullItemIdAndThenStatusBadRequest() throws Exception {
        bookingBuilder.itemId(null);

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingBuilder.buildDtoIn()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookingBuilder.bookerId()))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).add(any(), anyLong());
    }

    @Test
    void approveCorrectAndThenStatusOkAndJsonBody() throws Exception {
        boolean approved = true;
        bookingBuilder.status(BookingStatus.APPROVED);

        when(bookingService.approve(bookingBuilder.id, bookingBuilder.bookerId(), approved))
                .thenReturn(bookingBuilder.buildDtoOut());

        mvc.perform(patch("/bookings/{bookingId}", bookingBuilder.id)
                        .param("approved", String.valueOf(approved))
                        .header("X-Sharer-User-Id", bookingBuilder.bookerId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingBuilder.buildDtoOut())));
    }

    @Test
    void findByIdAndThenStatusOkAndJsonBody() throws Exception {
        BookingDtoOut expectedDto = bookingBuilder.buildDtoOut();

        when(bookingService.findById(bookingBuilder.id, bookingBuilder.bookerId))
                .thenReturn(expectedDto);

        mvc.perform(get("/bookings/{bookingId}", bookingBuilder.id)
                        .header("X-Sharer-User-Id", bookingBuilder.bookerId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedDto)));
    }

    @Test
    void findByBookerCorrectAndThenStatusOkAndJsonArrayBody() throws Exception {
        BookingDtoOut expectedDto = bookingBuilder.buildDtoOut();
        BookingSearchState searchState = BookingSearchState.WAITING;
        long from  = 10L;
        Integer size = 100;

        when(bookingService.findByBooker(bookingBuilder.bookerId, searchState, from, size))
                .thenReturn(List.of(expectedDto));

        mvc.perform(get("/bookings")
                        .param("state", searchState.toString())
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", bookingBuilder.bookerId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(expectedDto))));
    }

    @Test
    void findByBookerWithoutPaginationParamsAndThenStatusOkAndJsonArrayBody() throws Exception {
        BookingDtoOut expectedDto = bookingBuilder.buildDtoOut();
        BookingSearchState searchState = BookingSearchState.WAITING;

        when(bookingService.findByBooker(bookingBuilder.bookerId, searchState, 0, null))
                .thenReturn(List.of(expectedDto));

        mvc.perform(get("/bookings")
                        .param("state", searchState.toString())
                        .header("X-Sharer-User-Id", bookingBuilder.bookerId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(expectedDto))));
    }

    @Test
    void findByBookerWithIncorrectSearchStateAndThenStatusBadResponse() throws Exception {
        mvc.perform(get("/bookings")
                        .param("state", "unknown_state")
                        .header("X-Sharer-User-Id", bookingBuilder.bookerId()))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findByBooker(anyLong(), any(), anyLong(), any());
    }

    @Test
    void findByBookerWithNegativeFromAndThenStatusBadResponse() throws Exception {
        mvc.perform(get("/bookings")
                        .param("state", BookingSearchState.WAITING.toString())
                        .param("from", "-1")
                        .header("X-Sharer-User-Id", bookingBuilder.bookerId()))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findByBooker(anyLong(), any(), anyLong(), any());
    }

    @Test
    void findByBookerWithNegativeSizeAndThenStatusBadResponse() throws Exception {
        mvc.perform(get("/bookings")
                        .param("state", BookingSearchState.WAITING.toString())
                        .param("size", "-1")
                        .header("X-Sharer-User-Id", bookingBuilder.bookerId()))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findByBooker(anyLong(), any(), anyLong(), any());
    }

    @Test
    void findByBookerWithZeroSizeAndThenStatusBadResponse() throws Exception {
        mvc.perform(get("/bookings")
                        .param("state", BookingSearchState.WAITING.toString())
                        .param("size", "0")
                        .header("X-Sharer-User-Id", bookingBuilder.bookerId()))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findByBooker(anyLong(), any(), anyLong(), any());
    }

    @Test
    void findByOwnerCorrectAndThenStatusOkAndJsonArrayBody() throws Exception {
        BookingDtoOut expectedDto = bookingBuilder.buildDtoOut();
        BookingSearchState searchState = BookingSearchState.WAITING;
        long from  = 10L;
        Integer size = 100;

        when(bookingService.findByOwner(bookingBuilder.itemOwnerId(), searchState, from, size))
                .thenReturn(List.of(expectedDto));

        mvc.perform(get("/bookings/owner")
                        .param("state", searchState.toString())
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", bookingBuilder.itemOwnerId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(expectedDto))));
    }

    @Test
    void findByOwnerWithoutPaginationParamsAndThenStatusOkAndJsonArrayBody() throws Exception {
        BookingDtoOut expectedDto = bookingBuilder.buildDtoOut();
        BookingSearchState searchState = BookingSearchState.WAITING;

        when(bookingService.findByOwner(bookingBuilder.itemOwnerId(), searchState, 0, null))
                .thenReturn(List.of(expectedDto));

        mvc.perform(get("/bookings/owner")
                        .param("state", searchState.toString())
                        .header("X-Sharer-User-Id", bookingBuilder.itemOwnerId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(expectedDto))));
    }

    @Test
    void findByOwnerWithIncorrectSearchStateAndThenStatusBadResponse() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .param("state", "unknown_state")
                        .header("X-Sharer-User-Id", bookingBuilder.itemOwnerId()))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findByOwner(anyLong(), any(), anyLong(), any());
    }

    @Test
    void findByOwnerWithNegativeFromAndThenStatusBadResponse() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .param("state", BookingSearchState.WAITING.toString())
                        .param("from", "-1")
                        .header("X-Sharer-User-Id", bookingBuilder.itemOwnerId()))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findByOwner(anyLong(), any(), anyLong(), any());
    }

    @Test
    void findByOwnerWithNegativeSizeAndThenStatusBadResponse() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .param("state", BookingSearchState.WAITING.toString())
                        .param("size", "-1")
                        .header("X-Sharer-User-Id", bookingBuilder.itemOwnerId()))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findByOwner(anyLong(), any(), anyLong(), any());
    }

    @Test
    void findByOwnerWithZeroSizeAndThenStatusBadResponse() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .param("state", BookingSearchState.WAITING.toString())
                        .param("size", "0")
                        .header("X-Sharer-User-Id", bookingBuilder.itemOwnerId()))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findByOwner(anyLong(), any(), anyLong(), any());
    }
}