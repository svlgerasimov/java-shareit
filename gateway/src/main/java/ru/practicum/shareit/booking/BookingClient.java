package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingSearchState;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    ResponseEntity<Object> add(BookingDtoIn bookingDto, long userId) {
        return super.post("", userId, bookingDto);
    }

    ResponseEntity<Object> approve(long bookingId, long userId, boolean approved) {
        Map<String, Object> parameters = Map.of("approved", approved);
        return super.patch("/" + bookingId + "?approved={approved}", userId, parameters, null);
    }

    ResponseEntity<Object> findById(long bookingId, long userId) {
        return super.get("/" + bookingId, userId);
    }

    ResponseEntity<Object> findByBooker(long userId, BookingSearchState state, long from, int size) {
        Map<String, Object> parameters = Map.of(
                "state", state.toString(),
                "from", from,
                "size", size
        );
        return super.get("?state={state}&from={from}&{size}=size", userId, parameters);
    }

    ResponseEntity<Object> findByOwner(long userId, BookingSearchState state, long from, int size) {
        Map<String, Object> parameters = Map.of(
                "state", state.toString(),
                "from", from,
                "size", size
        );
        return super.get("/owner?state={state}&from={from}&{size}=size", userId, parameters);
    }
}
