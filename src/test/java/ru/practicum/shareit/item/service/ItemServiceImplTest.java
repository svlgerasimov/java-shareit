package ru.practicum.shareit.item.service;

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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.AuthenticationErrorException;
import ru.practicum.shareit.exception.CustomValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Spy
    private static ItemDtoMapper itemDtoMapper = new ItemDtoMapperImpl();
    @Spy
    private static ItemPatchDtoMapper itemPatchDtoMapper = new ItemPatchDtoMapperImpl();
    @Spy
    private static CommentDtoMapper commentDtoMapper = new CommentDtoMapperImpl();
    @Spy
    private static BookingDtoShortMapper bookingDtoShortMapper = new BookingDtoShortMapperImpl();

    @NoArgsConstructor(staticName = "defaultBuilder")
    @AllArgsConstructor(staticName = "all")
    @Setter
    @Accessors(chain = true, fluent = true)
    private static class TestItemBuilder {
        private Long id = 1L;
        private String name = "name1";
        private String description = "description1";
        private Boolean available = true;
        private Long requestId = 11L;
        private Long ownerId = 21L;
        private Long lastBookingId = 31L;
        private Long nextBookingId = 41L;
        private List<TestCommentBuilder> testCommentBuilders = List.of(
                TestCommentBuilder.all(51L, "Comment1", LocalDateTime.now().minusDays(1),
                        61L, "author1", id),
                TestCommentBuilder.all(71L, "Comment2", LocalDateTime.now().minusDays(2),
                        81L, "author2", id)
                );

        public Item buildEntity() {
            Item item = new Item();
            item.setId(id);
            item.setName(name);
            item.setDescription(description);
            item.setAvailable(available);
            if (Objects.nonNull(requestId)) {
                ItemRequest itemRequest = new ItemRequest();
                itemRequest.setId(requestId);
                item.setRequest(itemRequest);
            }
            User owner = new User();
            owner.setId(ownerId);
            item.setOwner(owner);
            return item;
        }

        public Booking buildLastBooking() {
            if (Objects.isNull(lastBookingId)) {
                return null;
            }
            Booking lastBooking = new Booking();
            lastBooking.setId(lastBookingId);
            Item item = new Item();
            item.setId(id);
            lastBooking.setItem(item);
            return lastBooking;
        }

        public Booking buildNextBooking() {
            if (Objects.isNull(nextBookingId)) {
                return null;
            }
            Booking nextBooking = new Booking();
            nextBooking.setId(nextBookingId);
            Item item = new Item();
            item.setId(id);
            nextBooking.setItem(item);
            return nextBooking;
        }

        public List<Comment> buildComments() {
            return testCommentBuilders.stream()
                    .map(TestCommentBuilder::buildEntity)
                    .collect(Collectors.toList());
        }

        public ItemDto buildDto() {
            return new ItemDto(id, name, description, available, requestId);
        }

        public ItemDtoOutExtended buildDtoOutExtended() {
            return new ItemDtoOutExtended(id, name, description, available, requestId,
                    Objects.isNull(lastBookingId) ? null :
                            new BookingDtoShort(lastBookingId, null, null, null, null),
                    Objects.isNull(nextBookingId) ? null :
                            new BookingDtoShort(nextBookingId, null, null, null, null),
                    testCommentBuilders.stream().map(TestCommentBuilder::buildDtoOut).collect(Collectors.toList()));
        }
    }

    @NoArgsConstructor(staticName = "defaultBuilder")
    @AllArgsConstructor(staticName = "all")
    @Setter
    @Accessors(chain = true, fluent = true)
    private static class TestCommentBuilder {
        private Long id = 101L;
        private String text = "comment text 1";
        private LocalDateTime created = LocalDateTime.of(2020, 1, 1, 1, 1);
        private Long authorId = 111L;
        private String authorName = "authorName1";
        private Long itemId = 121L;

        public Comment buildEntity() {
            Comment comment = new Comment();
            comment.setId(id);
            comment.setText(text);
            comment.setCreated(created);
            User author = new User();
            author.setId(authorId);
            author.setName(authorName);
            comment.setAuthor(author);
            Item item = new Item();
            item.setId(itemId);
            comment.setItem(item);
            return comment;
        }

        public CommentDtoOut buildDtoOut() {
            return new CommentDtoOut(id, text, authorName, created);
        }
    }

    @BeforeAll
    static void beforeAll() {
        ReflectionTestUtils.setField(itemDtoMapper, "bookingDtoShortMapper", bookingDtoShortMapper);
        ReflectionTestUtils.setField(itemDtoMapper, "commentDtoMapper", commentDtoMapper);
    }

    @Test
    void addWithCorrectDtoAndThenReturnDto() {
        TestItemBuilder itemBuilder = TestItemBuilder.defaultBuilder();
        ItemDto expectedDto = itemBuilder.buildDto();
        Item outputEntity = itemBuilder.buildEntity();
        itemBuilder.id(null);
        ItemDto inputDto = itemBuilder.buildDto();
        Item inputEntity = itemBuilder.buildEntity();
        Long ownerId = inputEntity.getOwner().getId();
        Long requestId = inputEntity.getRequest().getId();

        Mockito.when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(inputEntity.getOwner()));
        Mockito.when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.of(inputEntity.getRequest()));
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(outputEntity);

        assertEquals(expectedDto, itemService.add(inputDto, ownerId));

        Mockito.verify(itemRepository)
                .save(Mockito.argThat(item -> Objects.nonNull(item) &&
                        Objects.equals(item.getId(), inputEntity.getId()) &&
                        Objects.equals(item.getName(), inputEntity.getName()) &&
                        Objects.equals(item.getDescription(), inputEntity.getDescription()) &&
                        Objects.equals(item.getAvailable(), inputEntity.getAvailable()) &&
                        Objects.nonNull(item.getOwner()) &&
                        Objects.equals(item.getOwner().getId(), inputEntity.getOwner().getId()) &&
                        Objects.nonNull(item.getRequest()) &&
                        Objects.equals(item.getRequest().getId(), inputEntity.getRequest().getId())));
    }

    @Test
    void addWithCorrectDtoNullRequestAndThenReturnDtoWithNullRequestDto() {
        TestItemBuilder itemBuilder = TestItemBuilder.defaultBuilder().requestId(null);
        ItemDto expectedDto = itemBuilder.buildDto();
        Item outputEntity = itemBuilder.buildEntity();
        itemBuilder.id(null);
        ItemDto inputDto = itemBuilder.buildDto();
        Item inputEntity = itemBuilder.buildEntity();
        Long ownerId = inputEntity.getOwner().getId();

        Mockito.when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(inputEntity.getOwner()));
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(outputEntity);

        assertEquals(expectedDto, itemService.add(inputDto, ownerId));

        Mockito.verify(itemRequestRepository, Mockito.never()).findById(Mockito.anyLong());

        Mockito.verify(itemRepository)
                .save(Mockito.argThat(item -> Objects.nonNull(item) &&
                        Objects.equals(item.getId(), inputEntity.getId()) &&
                        Objects.equals(item.getName(), inputEntity.getName()) &&
                        Objects.equals(item.getDescription(), inputEntity.getDescription()) &&
                        Objects.equals(item.getAvailable(), inputEntity.getAvailable()) &&
                        Objects.nonNull(item.getOwner()) &&
                        Objects.equals(item.getOwner().getId(), inputEntity.getOwner().getId()) &&
                        Objects.isNull(item.getRequest())));
    }

    @Test
    void addWithIncorrectOwnerAndThenThrowNotFoundException() {
        TestItemBuilder itemBuilder = TestItemBuilder.defaultBuilder().requestId(null);
        Item outputEntity = itemBuilder.buildEntity();
        itemBuilder.id(null);
        ItemDto inputDto = itemBuilder.buildDto();
        Long ownerId = outputEntity.getOwner().getId();

        Mockito.when(userRepository.findById(ownerId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.add(inputDto, ownerId));
    }

    @Test
    void addWithIncorrectRequestAndThenThrowNotFoundException() {
        TestItemBuilder itemBuilder = TestItemBuilder.defaultBuilder();
        Item outputEntity = itemBuilder.buildEntity();
        itemBuilder.id(null);
        ItemDto inputDto = itemBuilder.buildDto();
        Long ownerId = outputEntity.getOwner().getId();
        Long requestId = outputEntity.getRequest().getId();

        Mockito.when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(outputEntity.getOwner()));
        Mockito.when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.add(inputDto, ownerId));
    }

    @Test
    void patchWithNameAndThenReturnPatchedDto() {
        TestItemBuilder itemBuilder = TestItemBuilder.defaultBuilder();
        Item initialEntity = itemBuilder.buildEntity();
        String patchName = initialEntity.getName() + " updated";
        itemBuilder.name(patchName);
        ItemDto expectedDto = itemBuilder.buildDto();
        long itemId = initialEntity.getId();
        long ownerId = initialEntity.getOwner().getId();

        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(initialEntity));

        assertEquals(expectedDto,
                itemService.patch(itemId, new ItemPatchDto(patchName, null, null), ownerId));
    }

    @Test
    void patchWithDescriptionAndThenReturnPatchedDto() {
        TestItemBuilder itemBuilder = TestItemBuilder.defaultBuilder();
        Item initialEntity = itemBuilder.buildEntity();
        String patchDescription = initialEntity.getDescription() + " updated";
        itemBuilder.description(patchDescription);
        ItemDto expectedDto = itemBuilder.buildDto();
        long itemId = initialEntity.getId();
        long ownerId = initialEntity.getOwner().getId();

        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(initialEntity));

        assertEquals(expectedDto,
                itemService.patch(itemId, new ItemPatchDto(null, patchDescription, null), ownerId));
    }

    @Test
    void patchWithAvailableAndThenReturnPatchedDto() {
        TestItemBuilder itemBuilder = TestItemBuilder.defaultBuilder();
        Item initialEntity = itemBuilder.buildEntity();
        Boolean patchAvailable = !initialEntity.getAvailable();
        itemBuilder.available(patchAvailable);
        ItemDto expectedDto = itemBuilder.buildDto();
        long itemId = initialEntity.getId();
        long ownerId = initialEntity.getOwner().getId();

        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(initialEntity));

        assertEquals(expectedDto,
                itemService.patch(itemId, new ItemPatchDto(null, null, patchAvailable), ownerId));
    }

    @Test
    void patchWithAllFieldsNullAndThenReturnEqualDto() {
        TestItemBuilder itemBuilder = TestItemBuilder.defaultBuilder();
        Item initialEntity = itemBuilder.buildEntity();
        ItemDto expectedDto = itemBuilder.buildDto();
        long itemId = initialEntity.getId();
        long ownerId = initialEntity.getOwner().getId();

        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(initialEntity));

        assertEquals(expectedDto,
                itemService.patch(itemId, new ItemPatchDto(null, null, null), ownerId));
    }

    @Test
    void patchWithIncorrectItemIdAndThenThrowNotFoundException() {
        TestItemBuilder itemBuilder = TestItemBuilder.defaultBuilder();
        Item initialEntity = itemBuilder.buildEntity();
        long itemId = initialEntity.getId() + 1;
        long ownerId = initialEntity.getOwner().getId();

        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.patch(itemId, new ItemPatchDto(null, null, null), ownerId));
    }

    @Test
    void patchWithWrongOwnerAndThenThrowAuthenticationErrorException() {
        TestItemBuilder itemBuilder = TestItemBuilder.defaultBuilder();
        Item initialEntity = itemBuilder.buildEntity();
        long itemId = initialEntity.getId();
        long ownerId = initialEntity.getOwner().getId();

        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(initialEntity));

        assertThrows(AuthenticationErrorException.class, () -> itemService.patch(itemId,
                new ItemPatchDto(null, null, null), ownerId + 1));
    }

    @Test
    void getByIdCorrectWithOwnerUserIdAndThenReturnExtendedDtoWithBookings() {
        TestItemBuilder itemBuilder = TestItemBuilder.defaultBuilder();
        Item itemEntity = itemBuilder.buildEntity();
        ItemDtoOutExtended expectedItemDto = itemBuilder.buildDtoOutExtended();
        Long itemId = itemEntity.getId();
        Long userId = itemEntity.getOwner().getId();

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(itemEntity.getOwner()));
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(itemEntity));
        Mockito.when(bookingRepository.findFirstByItemAndStartLessThanEqualAndStatusIs(
                Mockito.any(Item.class), Mockito.any(LocalDateTime.class),
                        Mockito.any(BookingStatus.class), Mockito.any(Sort.class)))
                .thenReturn(Optional.ofNullable(itemBuilder.buildLastBooking()));
        Mockito.when(bookingRepository.findFirstByItemAndStartAfterAndStatusIs(
                Mockito.any(Item.class), Mockito.any(LocalDateTime.class),
                        Mockito.any(BookingStatus.class), Mockito.any(Sort.class)))
                .thenReturn(Optional.ofNullable(itemBuilder.buildNextBooking()));
        Mockito.when(commentRepository.findAllByItem(Mockito.any(Item.class)))
                .thenReturn(itemBuilder.buildComments());

        LocalDateTime minCurTime = LocalDateTime.now();
        assertEquals(expectedItemDto, itemService.getById(itemId, userId));
        LocalDateTime maxCurTime = LocalDateTime.now();

        Mockito.verify(bookingRepository).findFirstByItemAndStartLessThanEqualAndStatusIs(
                Mockito.argThat(item -> Objects.nonNull(item) &&
                        Objects.equals(item.getId(), itemEntity.getId())),
                Mockito.argThat(localDateTime -> !minCurTime.isAfter(localDateTime) &&
                        !maxCurTime.isBefore(localDateTime)),
                Mockito.eq(BookingStatus.APPROVED),
                Mockito.eq(Sort.by(Sort.Direction.DESC, "start"))
        );

        Mockito.verify(bookingRepository).findFirstByItemAndStartAfterAndStatusIs(
                Mockito.argThat(item -> Objects.nonNull(item) &&
                        Objects.equals(item.getId(), itemEntity.getId())),
                Mockito.argThat(localDateTime -> !minCurTime.isAfter(localDateTime) &&
                        !maxCurTime.isBefore(localDateTime)),
                Mockito.eq(BookingStatus.APPROVED),
                Mockito.eq(Sort.by(Sort.Direction.ASC, "start"))
        );

        Mockito.verify(commentRepository).findAllByItem(
                Mockito.argThat(item -> Objects.nonNull(item) &&
                        Objects.equals(item.getId(), itemEntity.getId()))
        );
    }

    @Test
    void getByIdCorrectWithUserOtherThanOwnerAndThenReturnExtendedDtoWithoutBookings() {
        TestItemBuilder itemBuilder = TestItemBuilder.defaultBuilder();
        Item itemEntity = itemBuilder.buildEntity();
        List<Comment> commentEntities = itemBuilder.buildComments();
        itemBuilder.lastBookingId(null).nextBookingId(null);
        ItemDtoOutExtended expectedItemDto = itemBuilder.buildDtoOutExtended();
        Long itemId = itemEntity.getId();
        long userId = itemEntity.getOwner().getId() + 1;
        User userEntity = new User();
        userEntity.setId(userId);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(userEntity));
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(itemEntity));
        Mockito.when(commentRepository.findAllByItem(Mockito.any(Item.class)))
                .thenReturn(commentEntities);

        assertEquals(expectedItemDto, itemService.getById(itemId, userId));

        Mockito.verify(commentRepository).findAllByItem(
                Mockito.argThat(item -> Objects.nonNull(item) &&
                        Objects.equals(item.getId(), itemEntity.getId()))
        );
    }

    @Test
    void getByIdWithIncorrectUserIdAndThenThrowNotFoundException() {
        TestItemBuilder itemBuilder = TestItemBuilder.defaultBuilder();
        Item itemEntity = itemBuilder.buildEntity();
        Long itemId = itemEntity.getId();
        long userId = itemEntity.getOwner().getId() + 1;

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getById(itemId, userId));
    }

    @Test
    void getByIdWithIncorrectItemIdAndThenThrowNotFoundException() {
        TestItemBuilder itemBuilder = TestItemBuilder.defaultBuilder();
        Item itemEntity = itemBuilder.buildEntity();
        long itemId = itemEntity.getId() + 1;
        long userId = itemEntity.getOwner().getId();
        User userEntity = new User();
        userEntity.setId(userId);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(userEntity));
        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getById(itemId, userId));
    }

    @Test
    void getAllCorrectWithPaginationAndThenReturnDtoList() {
        TestItemBuilder itemBuilder = TestItemBuilder.defaultBuilder();
        Item itemEntity = itemBuilder.buildEntity();
        long from = 0;
        Integer size = 1;

        Mockito.when(userRepository.findById(itemEntity.getOwner().getId()))
                .thenReturn(Optional.of(itemEntity.getOwner()));
        Mockito.when(itemRepository.findAllByOwner(Mockito.any(User.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(itemEntity));
        Mockito.when(commentRepository.findAllByItemIn(Mockito.anyList()))
                .thenReturn(itemBuilder.buildComments());
        Mockito.when(bookingRepository.findAllByItemInAndStartLessThanEqualAndStatusIs(
                Mockito.anyList(), Mockito.any(LocalDateTime.class),
                        Mockito.any(BookingStatus.class), Mockito.any(Sort.class)))
                .thenReturn(List.of(itemBuilder.buildLastBooking()));
        Mockito.when(bookingRepository.findAllByItemInAndStartAfterAndStatusIs(
                        Mockito.anyList(), Mockito.any(LocalDateTime.class),
                        Mockito.any(BookingStatus.class), Mockito.any(Sort.class)))
                .thenReturn(List.of(itemBuilder.buildNextBooking()));

        assertEquals(List.of(itemBuilder.buildDtoOutExtended()),
                itemService.getAll(itemEntity.getOwner().getId(), from, size));

        Mockito.verify(itemRepository, Mockito.never())
                .findAllByOwner(Mockito.any(User.class), Mockito.any(Sort.class));
    }

    @Test
    void getAllCorrectWithoutPaginationAndThenReturnDtoList() {
        TestItemBuilder itemBuilder = TestItemBuilder.defaultBuilder();
        Item itemEntity = itemBuilder.buildEntity();
        long from = 0;
        Integer size = null;

        Mockito.when(userRepository.findById(itemEntity.getOwner().getId()))
                .thenReturn(Optional.of(itemEntity.getOwner()));
        Mockito.when(itemRepository.findAllByOwner(Mockito.any(User.class), Mockito.any(Sort.class)))
                .thenReturn(List.of(itemEntity));
        Mockito.when(commentRepository.findAllByItemIn(Mockito.anyList()))
                .thenReturn(itemBuilder.buildComments());
        Mockito.when(bookingRepository.findAllByItemInAndStartLessThanEqualAndStatusIs(
                        Mockito.anyList(), Mockito.any(LocalDateTime.class),
                        Mockito.any(BookingStatus.class), Mockito.any(Sort.class)))
                .thenReturn(List.of(itemBuilder.buildLastBooking()));
        Mockito.when(bookingRepository.findAllByItemInAndStartAfterAndStatusIs(
                        Mockito.anyList(), Mockito.any(LocalDateTime.class),
                        Mockito.any(BookingStatus.class), Mockito.any(Sort.class)))
                .thenReturn(List.of(itemBuilder.buildNextBooking()));

        assertEquals(List.of(itemBuilder.buildDtoOutExtended()),
                itemService.getAll(itemEntity.getOwner().getId(), from, size));

        Mockito.verify(itemRepository, Mockito.never())
                .findAllByOwner(Mockito.any(User.class), Mockito.any(Pageable.class));
    }

    @Test
    void getAllWithIncorrectUserIdAndThenThrowNotFoundException() {
        long userId = 1L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getAll(userId, 0 , 1));
    }

    @Test
    void searchWithPaginationAndThenReturnListOfDto() {
        TestItemBuilder itemBuilder = TestItemBuilder.defaultBuilder();
        Item itemEntity = itemBuilder.buildEntity();
        String text = "TeXt";
        String lowerCaseText = "text";
        long from = 0;
        int size = 1;

        Mockito.when(itemRepository.search(lowerCaseText, PageRequest.of((int) (from / size), size)))
                .thenReturn(List.of(itemEntity));

        assertEquals(List.of(itemBuilder.buildDto()),
                itemService.search(text, from, size));

        Mockito.verify(itemRepository, Mockito.never())
                .search(Mockito.any(String.class), Mockito.eq(Pageable.unpaged()));
    }

    @Test
    void searchWithoutPaginationAndThenReturnListOfDto() {
        TestItemBuilder itemBuilder = TestItemBuilder.defaultBuilder();
        Item itemEntity = itemBuilder.buildEntity();
        String text = "TeXt";
        String lowerCaseText = "text";
        long from = 0;
        Integer size = null;

        Mockito.when(itemRepository.search(lowerCaseText, Pageable.unpaged()))
                .thenReturn(List.of(itemEntity));

        assertEquals(List.of(itemBuilder.buildDto()),
                itemService.search(text, from, size));

        Mockito.verify(itemRepository, Mockito.never())
                .search(Mockito.any(String.class), Mockito.any(PageRequest.class));
    }

    @Test
    void addCommentCorrectAndThenReturnDto() {
        TestCommentBuilder builder = TestCommentBuilder.defaultBuilder();
        CommentDtoOut expectedDto = builder.buildDtoOut();
        Comment outputEntity = builder.buildEntity();
        builder.id(null);
        Comment inputEntity = builder.buildEntity();
        String text = inputEntity.getText();
        Long itemId = inputEntity.getItem().getId();
        Long authorId = inputEntity.getAuthor().getId();

        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(inputEntity.getItem()));
        Mockito.when(userRepository.findById(authorId))
                .thenReturn(Optional.of(inputEntity.getAuthor()));
        Mockito.when(bookingRepository.findFirstByItemAndBookerAndEndBefore(Mockito.any(Item.class),
                Mockito.any(User.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(Optional.of(new Booking()));
        Mockito.when(commentRepository.save(Mockito.any(Comment.class)))
                .thenReturn(outputEntity);

        LocalDateTime startCurTime = LocalDateTime.now();
        assertEquals(expectedDto,
                itemService.addComment(new CommentDtoIn(text), itemId, authorId));
        LocalDateTime endCurTime = LocalDateTime.now();

        Mockito.verify(commentRepository).save(Mockito.argThat(comment -> Objects.nonNull(comment) &&
                Objects.equals(comment.getId(), inputEntity.getId()) &&
                Objects.equals(comment.getText(), inputEntity.getText()) &&
                !comment.getCreated().isBefore(startCurTime) &&
                !comment.getCreated().isAfter(endCurTime) &&
                Objects.nonNull(comment.getItem()) &&
                Objects.equals(comment.getItem().getId(), itemId) &&
                Objects.nonNull(comment.getAuthor()) &&
                Objects.equals(comment.getAuthor().getId(), authorId) &&
                Objects.equals(comment.getAuthor().getName(), inputEntity.getAuthor().getName())));

        Mockito.verify(bookingRepository).findFirstByItemAndBookerAndEndBefore(
                Mockito.argThat(item -> Objects.nonNull(item) &&
                        Objects.equals(item.getId(), itemId)),
                Mockito.argThat(user -> Objects.nonNull(user) &&
                        Objects.equals(user.getId(), authorId) &&
                        Objects.equals(user.getName(), inputEntity.getAuthor().getName())),
                Mockito.any(LocalDateTime.class)
        );
    }

    @Test
    void addCommentWithIncorrectItemIdAndThenThrowNotFoundException() {
        TestCommentBuilder builder = TestCommentBuilder.defaultBuilder();
        builder.id(null);
        Comment inputEntity = builder.buildEntity();
        long itemId = inputEntity.getItem().getId() + 1;
        String text = inputEntity.getText();
        Long authorId = inputEntity.getAuthor().getId();

        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.addComment(new CommentDtoIn(text), itemId, authorId));
    }

    @Test
    void addCommentWithIncorrectAuthorIdAndThenThrowNotFoundException() {
        TestCommentBuilder builder = TestCommentBuilder.defaultBuilder();
        builder.id(null);
        Comment inputEntity = builder.buildEntity();
        String text = inputEntity.getText();
        Long itemId = inputEntity.getItem().getId();
        long authorId = inputEntity.getAuthor().getId() + 1;

        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(inputEntity.getItem()));
        Mockito.when(userRepository.findById(authorId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.addComment(new CommentDtoIn(text), itemId, authorId));
    }

    @Test
    void addCommentByUserWithNoFinishedBookingsAndThenThrowCustomValidationException() {
        TestCommentBuilder builder = TestCommentBuilder.defaultBuilder();
        builder.id(null);
        Comment inputEntity = builder.buildEntity();
        String text = inputEntity.getText();
        Long itemId = inputEntity.getItem().getId();
        Long authorId = inputEntity.getAuthor().getId();

        Mockito.when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(inputEntity.getItem()));
        Mockito.when(userRepository.findById(authorId))
                .thenReturn(Optional.of(inputEntity.getAuthor()));
        Mockito.when(bookingRepository.findFirstByItemAndBookerAndEndBefore(Mockito.any(Item.class),
                        Mockito.any(User.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        assertThrows(CustomValidationException.class,
                () -> itemService.addComment(new CommentDtoIn(text), itemId, authorId));
    }
}