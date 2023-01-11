package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutExtended;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestDtoMapper itemRequestDtoMapper;

    @Override
    @Transactional
    public ItemRequestDtoOut add(ItemRequestDtoIn dto, long userId) {
        User requestor = getUser(userId);
        ItemRequest request = itemRequestDtoMapper.fromDto(dto);
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        request = itemRequestRepository.save(request);
        log.debug("Add item request {}", request);
        return itemRequestDtoMapper.toDto(request);
    }

    @Override
    public ItemRequestDtoOutExtended findById(long id, long userId) {
        getUser(userId);
        ItemRequest itemRequest = getItemRequest(id);
        List<Item> items = itemRepository.findAllByRequest(itemRequest);
        return itemRequestDtoMapper.toExtendedDto(itemRequest, items);
    }

    @Override
    public List<ItemRequestDtoOutExtended> findByRequestor(long requestorId) {
        User requestor = getUser(requestorId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor(requestor,
                Sort.by(Sort.Direction.DESC, "created"));
        List<Item> items = itemRepository.findAllByRequestIn(itemRequests);
        return itemRequestDtoMapper.toExtendedDto(itemRequests, formItemsByRequestIds(items));
    }

    @Override
    public List<ItemRequestDtoOutExtended> findByOtherUsers(long userId, long from, Integer size) {
        User exceptedRequestor = getUser(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> itemRequests =
                Objects.isNull(size) ?
                        itemRequestRepository.findAllByRequestorIsNot(exceptedRequestor, sort) :
                        itemRequestRepository.findAllByRequestorIsNot(exceptedRequestor,
                                PageRequest.of((int) (from / size), size, sort));
        List<Item> items = itemRepository.findAllByRequestIn(itemRequests);
        return itemRequestDtoMapper.toExtendedDto(itemRequests, formItemsByRequestIds(items));
    }

    private ItemRequest getItemRequest(long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Item request with id=" + requestId + " not found"));
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
    }

    private Map<Long, List<Item>> formItemsByRequestIds(List<Item> allItems) {
        return allItems.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId(), Collectors.toList()));
    }
}
