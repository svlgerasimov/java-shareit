package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody @Valid ItemRequestDtoIn itemRequestDto,
                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.add(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.findByRequestor(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllByOtherUsers(
            @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.findByOtherUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@PathVariable long requestId,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.findById(requestId, userId);
    }





}
