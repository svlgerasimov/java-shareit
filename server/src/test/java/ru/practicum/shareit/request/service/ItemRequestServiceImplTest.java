package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.dto.BookingDtoShortMapperImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDtoMapperImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemDtoMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Spy
    private static ItemRequestDtoMapper itemRequestDtoMapper = new ItemRequestDtoMapperImpl();

    @NoArgsConstructor(staticName = "defaultBuilder")
    @AllArgsConstructor(staticName = "all")
    @Setter
    @Accessors(chain = true, fluent = true)
    private static class TestRequestBuilder {
        private Long id = 1L;
        private String description = "description1";
        private LocalDateTime created = LocalDateTime.of(2020, 1, 1, 1, 1);
        private Long requestorId = 11L;
        private Long itemId1 = 101L;
        private Long itemOwnerId1 = 111L;

        public ItemRequest buildEntity() {
            ItemRequest request = new ItemRequest();
            request.setId(id);
            request.setDescription(description);
            request.setCreated(created);
            User requestor = new User();
            requestor.setId(requestorId);
            request.setRequestor(requestor);
//            request.setItems(Set.of(item1));
            return request;
        }

        public List<Item> buildItems(ItemRequest request) {
            Item item1 = new Item();
            item1.setId(itemId1);
            User owner1 = new User();
            owner1.setId(itemOwnerId1);
            item1.setOwner(owner1);
            item1.setRequest(request);
            return List.of(item1);
        }

        public ItemRequestDtoOut buildDtoOut() {
            return new ItemRequestDtoOut(id, description, created);
        }

        public ItemRequestDtoOutExtended buildDtoOutExtended() {
            return new ItemRequestDtoOutExtended(id, description, created,
                    List.of(new ItemDto(itemId1, null, null, null, id)));
        }
    }

    @BeforeAll
    static void beforeAll() {
        ItemDtoMapper itemDtoMapper = new ItemDtoMapperImpl();
        ReflectionTestUtils.setField(itemRequestDtoMapper, "itemDtoMapper", itemDtoMapper);
        ReflectionTestUtils.setField(itemDtoMapper, "bookingDtoShortMapper", new BookingDtoShortMapperImpl());
        ReflectionTestUtils.setField(itemDtoMapper, "commentDtoMapper", new CommentDtoMapperImpl());
    }

    @Test
    void addWithCorrectDtoAndThenSaveAndReturnDto() {
        TestRequestBuilder requestBuilder = TestRequestBuilder.defaultBuilder();
        ItemRequest savedEntity = requestBuilder.buildEntity();
        ItemRequestDtoOut expectedDto = requestBuilder.buildDtoOut();
        requestBuilder.id(null).created(null);
        ItemRequest inputEntity = requestBuilder.buildEntity();
        ItemRequestDtoIn inputDto = new ItemRequestDtoIn(inputEntity.getDescription());
        User requestor = inputEntity.getRequestor();
        long userId = requestor.getId();

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(requestor));
        Mockito.when(itemRequestRepository.save(Mockito.any()))
                .thenReturn(savedEntity);

        LocalDateTime minCreated = LocalDateTime.now();
        assertEquals(expectedDto, itemRequestService.add(inputDto, userId));
        LocalDateTime maxCreated = LocalDateTime.now();

        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .save(Mockito.argThat(
                        itemRequest -> Objects.nonNull(itemRequest) &&
                                Objects.equals(itemRequest.getId(), inputEntity.getId()) &&
                                Objects.equals(itemRequest.getDescription(), inputEntity.getDescription()) &&
                                !itemRequest.getCreated().isBefore(minCreated) &&
                                !itemRequest.getCreated().isAfter(maxCreated) &&
                                Objects.nonNull(itemRequest.getRequestor()) &&
                                Objects.equals(itemRequest.getRequestor().getId(), inputEntity.getRequestor().getId())
                ));
    }

    @Test
    void addWithIncorrectRequestorIdAndThenThrowNotFoundException() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.add(new ItemRequestDtoIn("description"), 1L));

        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findByIdCorrectAndThenReturnDto() {
        TestRequestBuilder requestBuilder = TestRequestBuilder.defaultBuilder();
        ItemRequest entity = requestBuilder.buildEntity();
        ItemRequestDtoOutExtended dto = requestBuilder.buildDtoOutExtended();
        long userId = entity.getRequestor().getId() + 1;
        User user = new User();
        user.setId(userId);

        Mockito.lenient().when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        Mockito.lenient().when(itemRequestRepository.findById(entity.getId()))
                .thenReturn(Optional.of(entity));
        Mockito.lenient().when(itemRepository.findAllByRequest(Mockito.argThat(itemRequest ->
                        Objects.nonNull(itemRequest) &&
                        Objects.nonNull(itemRequest.getId()) &
                        Objects.equals(itemRequest.getDescription(), entity.getDescription()) &&
                        Objects.equals(itemRequest.getCreated(), entity.getCreated()))))
                .thenReturn(requestBuilder.buildItems(entity));

        assertEquals(dto, itemRequestService.findById(entity.getId(), userId));
    }

    @Test
    void findByIdWithIncorrectUserIdAndThenThrowNotFoundException() {
        TestRequestBuilder requestBuilder = TestRequestBuilder.defaultBuilder();
        ItemRequest entity = requestBuilder.buildEntity();
        long userId = entity.getRequestor().getId() + 1;
        User user = new User();
        user.setId(userId);

        Mockito.lenient().when(userRepository.findById(userId))
                .thenReturn(Optional.empty());
        Mockito.lenient().when(itemRequestRepository.findById(entity.getId()))
                .thenReturn(Optional.of(entity));

        assertThrows(NotFoundException.class,
                () -> itemRequestService.findById(entity.getId(), userId));

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(userId);
    }

    @Test
    void findByIdWithIncorrectRequestIdAndThenThrowNotFoundException() {
        TestRequestBuilder requestBuilder = TestRequestBuilder.defaultBuilder();
        ItemRequest entity = requestBuilder.buildEntity();
        long userId = entity.getRequestor().getId() + 1;
        User user = new User();
        user.setId(userId);
        long id = entity.getId() + 1;

        Mockito.lenient().when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        Mockito.lenient().when(itemRequestRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.findById(id, userId));

        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findById(id);
    }

    @Test
    void findByRequestorCorrectAndThenReturnListOfDto() {
        TestRequestBuilder requestBuilder = TestRequestBuilder.defaultBuilder();
        ItemRequest entity = requestBuilder.buildEntity();
        User requestor = entity.getRequestor();
        long requestorId = requestor.getId();
        ItemRequestDtoOutExtended dto = requestBuilder.buildDtoOutExtended();

        Mockito.when(userRepository.findById(requestorId))
                .thenReturn(Optional.of(requestor));
        Mockito.when(itemRequestRepository.findAllByRequestor(Mockito.any(), Mockito.any()))
                .thenReturn(List.of(entity));
        Mockito.when(itemRepository.findAllByRequestIn(Mockito.argThat(itemRequestList ->
                        Objects.nonNull(itemRequestList) &&
                        itemRequestList.size() == 1 &&
                        Objects.nonNull(itemRequestList.get(0).getId()) &&
                        Objects.equals(itemRequestList.get(0).getDescription(), entity.getDescription()) &&
                        Objects.equals(itemRequestList.get(0).getCreated(), entity.getCreated()))))
                .thenReturn(requestBuilder.buildItems(entity));

        assertEquals(List.of(dto), itemRequestService.findByRequestor(requestorId));

        Mockito.verify(itemRequestRepository, Mockito.atLeastOnce())
                .findAllByRequestor(
                        Mockito.argThat(user -> Objects.nonNull(user) &&
                                Objects.equals(user.getId(), requestor.getId())),
                        Mockito.eq(Sort.by(Sort.Direction.DESC, "created"))
                );
    }

    @Test
    void findByRequestorWithIncorrectRequestorIdAndThenThrowNotFoundException() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.findById(2L, 1L));
    }

    @Test
    void findByRequestorWithNoRequestsAndThenReturnEmptyList() {
        TestRequestBuilder requestBuilder = TestRequestBuilder.defaultBuilder();
        ItemRequest entity = requestBuilder.buildEntity();

        Mockito.when(userRepository.findById(entity.getRequestor().getId()))
                .thenReturn(Optional.of(entity.getRequestor()));
        Mockito.when(itemRequestRepository.findAllByRequestor(Mockito.any(), Mockito.any()))
                .thenReturn(List.of());

        assertEquals(Collections.emptyList(), itemRequestService.findByRequestor(entity.getRequestor().getId()));
    }

    @Test
    void findByOtherUsersCorrectWithPaginationAndThenReturnListOfDto() {
        TestRequestBuilder requestBuilder = TestRequestBuilder.defaultBuilder();
        ItemRequest entity = requestBuilder.buildEntity();
        long userId = entity.getRequestor().getId() + 1;
        User user = new User();
        user.setId(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findAllByRequestorIsNot(
                        Mockito.argThat(requestor -> Objects.nonNull(requestor) &&
                                Objects.equals(requestor.getId(), userId)),
                        Mockito.eq(PageRequest.of(0, 1, sort))))
                .thenReturn(List.of(entity));
        Mockito.when(itemRepository.findAllByRequestIn(Mockito.argThat(itemRequestList ->
                        Objects.nonNull(itemRequestList) &&
                                itemRequestList.size() == 1 &&
                                Objects.nonNull(itemRequestList.get(0).getId()) &&
                                Objects.equals(itemRequestList.get(0).getDescription(), entity.getDescription()) &&
                                Objects.equals(itemRequestList.get(0).getCreated(), entity.getCreated()))))
                .thenReturn(requestBuilder.buildItems(entity));

        assertEquals(List.of(requestBuilder.buildDtoOutExtended()),
                itemRequestService.findByOtherUsers(userId, 0, 1));
    }

    @Test
    void findByOtherUsersWithIncorrectUserIdAndThenThrowNotFoundException() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.findByOtherUsers(1L, 0, 1));
    }

}