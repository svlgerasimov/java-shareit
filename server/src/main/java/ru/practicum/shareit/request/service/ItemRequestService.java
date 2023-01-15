package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutExtended;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoOut add(ItemRequestDtoIn dto, long userId);

    ItemRequestDtoOutExtended findById(long id, long userId);

    List<ItemRequestDtoOutExtended> findByRequestor(long requestorId);

    List<ItemRequestDtoOutExtended> findByOtherUsers(long userId, long from, int size);
}
