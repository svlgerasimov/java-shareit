package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingSearchState;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private MockMvc mvc;

    private TestBookingBuilder bookingBuilder;

    @NoArgsConstructor(staticName = "defaultBuilder")
    @AllArgsConstructor(staticName = "all")
    @Setter
    @Getter
    @Accessors(chain = true, fluent = true)
    private static class TestBookingBuilder {
        private LocalDateTime start = LocalDateTime.now().plusDays(2);
        private LocalDateTime end = LocalDateTime.now().plusDays(4);
        private BookingStatus status = BookingStatus.WAITING;
        private Long itemId = 11L;
        private Long itemOwnerId = 21L;
        private Long bookerId = 31L;

        public BookingDtoIn buildDtoIn() {
            return new BookingDtoIn(start, end, itemId);
        }
    }

    @BeforeEach
    void setUp() {
        bookingBuilder = TestBookingBuilder.defaultBuilder();
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

        verify(bookingClient, never()).add(any(), anyLong());
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

        verify(bookingClient, never()).add(any(), anyLong());
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

        verify(bookingClient, never()).add(any(), anyLong());
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

        verify(bookingClient, never()).add(any(), anyLong());
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

        verify(bookingClient, never()).add(any(), anyLong());
    }

    @Test
    void findByBookerWithoutPaginationParamsAndThenDefaultParameters() throws Exception {
        BookingSearchState searchState = BookingSearchState.WAITING;

        mvc.perform(get("/bookings")
                        .param("state", searchState.toString())
                        .header("X-Sharer-User-Id", 1L));

        verify(bookingClient, times(1))
                .findByBooker(1L, BookingSearchState.WAITING, 0L, 10);
    }

    @Test
    void findByBookerWithIncorrectSearchStateAndThenStatusBadResponse() throws Exception {
        mvc.perform(get("/bookings")
                        .param("state", "unknown_state")
                        .header("X-Sharer-User-Id", bookingBuilder.bookerId()))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).findByBooker(anyLong(), any(), anyLong(), anyInt());
    }

    @Test
    void findByBookerWithNegativeFromAndThenStatusBadResponse() throws Exception {
        mvc.perform(get("/bookings")
                        .param("state", BookingSearchState.WAITING.toString())
                        .param("from", "-1")
                        .header("X-Sharer-User-Id", bookingBuilder.bookerId()))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).findByBooker(anyLong(), any(), anyLong(), anyInt());
    }

    @Test
    void findByBookerWithNegativeSizeAndThenStatusBadResponse() throws Exception {
        mvc.perform(get("/bookings")
                        .param("state", BookingSearchState.WAITING.toString())
                        .param("size", "-1")
                        .header("X-Sharer-User-Id", bookingBuilder.bookerId()))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).findByBooker(anyLong(), any(), anyLong(), anyInt());
    }

    @Test
    void findByBookerWithZeroSizeAndThenStatusBadResponse() throws Exception {
        mvc.perform(get("/bookings")
                        .param("state", BookingSearchState.WAITING.toString())
                        .param("size", "0")
                        .header("X-Sharer-User-Id", bookingBuilder.bookerId()))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).findByBooker(anyLong(), any(), anyLong(), anyInt());
    }

    @Test
    void findByOwnerWithoutPaginationParamsAndThenDefaultParams() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .param("state", BookingSearchState.WAITING.toString())
                        .header("X-Sharer-User-Id", 1L));

        verify(bookingClient, times(1))
                .findByOwner(1L, BookingSearchState.WAITING, 0, 10);
    }

    @Test
    void findByOwnerWithIncorrectSearchStateAndThenStatusBadResponse() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .param("state", "unknown_state")
                        .header("X-Sharer-User-Id", bookingBuilder.itemOwnerId()))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).findByOwner(anyLong(), any(), anyLong(), anyInt());
    }

    @Test
    void findByOwnerWithNegativeFromAndThenStatusBadResponse() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .param("state", BookingSearchState.WAITING.toString())
                        .param("from", "-1")
                        .header("X-Sharer-User-Id", bookingBuilder.itemOwnerId()))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).findByOwner(anyLong(), any(), anyLong(), anyInt());
    }

    @Test
    void findByOwnerWithNegativeSizeAndThenStatusBadResponse() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .param("state", BookingSearchState.WAITING.toString())
                        .param("size", "-1")
                        .header("X-Sharer-User-Id", bookingBuilder.itemOwnerId()))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).findByOwner(anyLong(), any(), anyLong(), anyInt());
    }

    @Test
    void findByOwnerWithZeroSizeAndThenStatusBadResponse() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .param("state", BookingSearchState.WAITING.toString())
                        .param("size", "0")
                        .header("X-Sharer-User-Id", bookingBuilder.itemOwnerId()))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).findByOwner(anyLong(), any(), anyLong(), anyInt());
    }
}