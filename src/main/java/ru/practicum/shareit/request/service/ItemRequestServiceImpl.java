package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
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

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
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
        log.debug("Add item request: " + request);
        return itemRequestDtoMapper.toDto(request);
    }

    @Override
    public ItemRequestDtoOutExtended findById(long id) {
        return itemRequestDtoMapper.toExtendedDto(getItemRequest(id));
    }

    @Override
    public List<ItemRequestDtoOutExtended> findByRequestor(long requestorId) {
        User requestor = getUser(requestorId);
        return itemRequestDtoMapper.toExtendedDto(
                itemRequestRepository.findAllByRequestor(requestor,
                        Sort.by(Sort.Direction.DESC, "created"))
        );
    }

    @Override
    public List<ItemRequestDtoOutExtended> findByOtherUsers(long userId, Long from, Integer size) {
        User exceptedRequestor = getUser(userId);
//        Pageable pageable = Objects.isNull(size) ? Pageable.unpaged()
        return itemRequestDtoMapper.toExtendedDto(
                itemRequestRepository.findAllByRequestorIsNot(exceptedRequestor,
                        PageRequest.of((int)(from / size), size, Sort.Direction.DESC, "created"))
        );
    }

    private ItemRequest getItemRequest(long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Item request with id=" + requestId + " not found"));
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
    }


}
