package ru.practicum.shareit.booking.service;

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
import ru.practicum.shareit.exception.CustomValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Spy
    private static BookingDtoMapper bookingDtoMapper = new BookingDtoMapperImpl();

    @NoArgsConstructor(staticName = "defaultBuilder")
    @AllArgsConstructor(staticName = "all")
    @Setter
    @Accessors(chain = true, fluent = true)
    private static class TestBookingBuilder {
        private Long id = 1L;
        private LocalDateTime start = LocalDateTime.now().minusDays(2);
        private LocalDateTime end = LocalDateTime.now().minusDays(1);
        private BookingStatus status = BookingStatus.WAITING;
        private Long itemId = 11L;
        private Boolean itemAvailable = true;
        private Long itemOwnerId = 21L;
        private Long bookerId = 31L;

        public Booking buildEntity() {
            Booking booking = new Booking();
            booking.setId(id);
            booking.setStart(start);
            booking.setEnd(end);
            booking.setStatus(status);
            Item item = new Item();
            item.setId(itemId);
            item.setAvailable(itemAvailable);
            User itemOwner = new User();
            itemOwner.setId(itemOwnerId);
            item.setOwner(itemOwner);
            booking.setItem(item);
            User booker = new User();
            booker.setId(bookerId);
            booking.setBooker(booker);
            return booking;
        }

        public BookingDtoIn buildDtoIn() {
            return new BookingDtoIn(start, end, itemId);
        }

        public BookingDtoOut buildDtoOut() {
            return new BookingDtoOut(id, start, end,
                    new ItemDto(itemId, null, null, itemAvailable, null),
                    new UserDto(bookerId, null, null),
                    status);
        }
    }

    @BeforeAll
    static void beforeAll() {
        ReflectionTestUtils.setField(bookingDtoMapper, "userDtoMapper", new UserDtoMapperImpl());
        ReflectionTestUtils.setField(bookingDtoMapper, "itemDtoMapper", new ItemDtoMapperImpl());
    }

    @Test
    void addCorrectAndThenSaveAndReturnDto() {
        TestBookingBuilder bookingBuilder = TestBookingBuilder.defaultBuilder();
        Booking outputEntity = bookingBuilder.buildEntity();
        BookingDtoOut expectedDto = bookingBuilder.buildDtoOut();
        bookingBuilder.id(null);
        Booking inputEntity = bookingBuilder.buildEntity();
        BookingDtoIn inputDto = bookingBuilder.buildDtoIn();
        long userId = inputEntity.getBooker().getId();

        Mockito.when(itemRepository.findByIdAndOwnerIdNot(inputDto.getItemId(), userId))
                .thenReturn(Optional.of(inputEntity.getItem()));
        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(inputEntity.getBooker()));
        Mockito.when(bookingRepository.save(Mockito.argThat(booking -> Objects.nonNull(booking) &&
                Objects.equals(booking.getId(), inputEntity.getId()) &&
                Objects.equals(booking.getStart(), inputEntity.getStart()) &&
                Objects.equals(booking.getEnd(), inputEntity.getEnd()) &&
                Objects.equals(booking.getStatus(), inputEntity.getStatus()) &&
                Objects.nonNull(booking.getBooker()) &&
                Objects.equals(booking.getBooker().getId(), inputEntity.getBooker().getId()) &&
                Objects.nonNull(booking.getItem()) &&
                Objects.equals(booking.getItem().getId(), inputEntity.getItem().getId()))))
                .thenReturn(outputEntity);

        assertEquals(expectedDto, bookingService.add(inputDto, userId));
    }

    @Test
    void addWithNoItemWithGivenIdAndOtherOwnerAndThenThrowNotFoundExcetpion() {
        TestBookingBuilder bookingBuilder = TestBookingBuilder.defaultBuilder().id(null);
        Booking inputEntity = bookingBuilder.buildEntity();
        BookingDtoIn inputDto = bookingBuilder.buildDtoIn();
        long userId = inputEntity.getBooker().getId();

        Mockito.lenient().when(itemRepository.findByIdAndOwnerIdNot(inputDto.getItemId(), userId))
                .thenReturn(Optional.empty());
        Mockito.lenient().when(userRepository.findById(userId))
                .thenReturn(Optional.of(inputEntity.getBooker()));

        assertThrows(NotFoundException.class,
                () -> bookingService.add(inputDto, userId));

        Mockito.verify(bookingRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void addWithUnavailableItemAndThenThrowCustomValidationException() {
        TestBookingBuilder bookingBuilder = TestBookingBuilder.defaultBuilder().itemAvailable(false).id(null);
        Booking inputEntity = bookingBuilder.buildEntity();
        BookingDtoIn inputDto = bookingBuilder.buildDtoIn();
        long userId = inputEntity.getBooker().getId();

        Mockito.lenient().when(itemRepository.findByIdAndOwnerIdNot(inputDto.getItemId(), userId))
                .thenReturn(Optional.of(inputEntity.getItem()));
        Mockito.lenient().when(userRepository.findById(userId))
                .thenReturn(Optional.of(inputEntity.getBooker()));

        assertThrows(CustomValidationException.class,
                () -> bookingService.add(inputDto, userId));

        Mockito.verify(bookingRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void addWithIncorrectUserIdAndThenThrowNotFoundException() {
        TestBookingBuilder bookingBuilder = TestBookingBuilder.defaultBuilder().id(null);
        Booking inputEntity = bookingBuilder.buildEntity();
        BookingDtoIn inputDto = bookingBuilder.buildDtoIn();
        long userId = inputEntity.getBooker().getId();

        Mockito.lenient().when(itemRepository.findByIdAndOwnerIdNot(inputDto.getItemId(), userId))
                .thenReturn(Optional.of(inputEntity.getItem()));
        Mockito.lenient().when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.add(inputDto, userId));

        Mockito.verify(bookingRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void approveCorrectWaitingBookingSetApprovedAndThenReturnDto() {
        TestBookingBuilder bookingBuilder = TestBookingBuilder.defaultBuilder().status(BookingStatus.WAITING);
        Booking entity = bookingBuilder.buildEntity();
        bookingBuilder.status(BookingStatus.APPROVED);
        BookingDtoOut expectedDto = bookingBuilder.buildDtoOut();
        long bookingId = entity.getId();
        long userId = entity.getItem().getOwner().getId();
        boolean approved = true;

        Mockito.when(bookingRepository.findByIdAndItemOwnerId(bookingId, userId))
                .thenReturn(Optional.of(entity));

        assertEquals(expectedDto, bookingService.approve(bookingId, userId, approved));
        assertEquals(BookingStatus.APPROVED, entity.getStatus());
    }

    @Test
    void approveCorrectWaitingBookingSetRejectedAndThenReturnDto() {
        TestBookingBuilder bookingBuilder = TestBookingBuilder.defaultBuilder().status(BookingStatus.WAITING);
        Booking entity = bookingBuilder.buildEntity();
        bookingBuilder.status(BookingStatus.REJECTED);
        BookingDtoOut expectedDto = bookingBuilder.buildDtoOut();
        long bookingId = entity.getId();
        long userId = entity.getItem().getOwner().getId();
        boolean approved = false;

        Mockito.when(bookingRepository.findByIdAndItemOwnerId(bookingId, userId))
                .thenReturn(Optional.of(entity));

        assertEquals(expectedDto, bookingService.approve(bookingId, userId, approved));
        assertEquals(BookingStatus.REJECTED, entity.getStatus());
    }

    @Test
    void approveWithNoBookingWithGivenIdAndOwnerIdAndThenThrowNotFoundException() {
        TestBookingBuilder bookingBuilder = TestBookingBuilder.defaultBuilder().status(BookingStatus.WAITING);
        Booking entity = bookingBuilder.buildEntity();
        long bookingId = entity.getId();
        long userId = entity.getItem().getOwner().getId() + 1;
        boolean approved = true;

        Mockito.when(bookingRepository.findByIdAndItemOwnerId(bookingId, userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.approve(bookingId, userId, approved));
        assertEquals(BookingStatus.WAITING, entity.getStatus());
    }

    @Test
    void approveBookingWithNotWaitingStatusAndThenThrowCustomValidationException() {
        TestBookingBuilder bookingBuilder = TestBookingBuilder.defaultBuilder().status(BookingStatus.APPROVED);
        Booking entity = bookingBuilder.buildEntity();
        long bookingId = entity.getId();
        long userId = entity.getItem().getOwner().getId();

        Mockito.when(bookingRepository.findByIdAndItemOwnerId(bookingId, userId))
                .thenReturn(Optional.of(entity));

        assertThrows(CustomValidationException.class,
                () -> bookingService.approve(bookingId, userId, true));
        assertEquals(BookingStatus.APPROVED, entity.getStatus());

        bookingBuilder.status(BookingStatus.REJECTED);
        entity = bookingBuilder.buildEntity();
        Mockito.when(bookingRepository.findByIdAndItemOwnerId(bookingId, userId))
                .thenReturn(Optional.of(entity));

        assertThrows(CustomValidationException.class,
                () -> bookingService.approve(bookingId, userId, true));
        assertEquals(BookingStatus.REJECTED, entity.getStatus());
    }

    @Test
    void findByIdCorrectAndThenReturnDto() {
        TestBookingBuilder bookingBuilder = TestBookingBuilder.defaultBuilder();
        Booking entity = bookingBuilder.buildEntity();
        BookingDtoOut expectedDto = bookingBuilder.buildDtoOut();
        long bookingId = entity.getId();
        long userId = entity.getBooker().getId();

        Mockito.when(bookingRepository.findByIdAndItemOwnerIdOrBookerId(bookingId, userId))
                .thenReturn(Optional.of(entity));

        assertEquals(expectedDto, bookingService.findById(bookingId, userId));
    }

    @Test
    void findByIdWithNoBookingWithGivenIdAndOwnerOrBookerIdAndThenThrowNotFoundException() {
        long bookingId = 1L;
        long userId = 2L;

        Mockito.when(bookingRepository.findByIdAndItemOwnerIdOrBookerId(bookingId, userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.findById(bookingId, userId));
    }

    @Test
    void findByBookerWithIncorrectBookerIdAndThenThrowNotFoundException() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> bookingService.findByBooker(1L, BookingSearchState.ALL, 0, 1));
    }

    @Test
    void findByBookerWithSearchStatePastAndPaginationAndThenReturnListOfDto() {
        TestBookingBuilder bookingBuilder = TestBookingBuilder.defaultBuilder();
        Booking entity = bookingBuilder.buildEntity();
        BookingDtoOut expectedDto = bookingBuilder.buildDtoOut();
        long bookerId = entity.getBooker().getId();
        BookingSearchState searchState = BookingSearchState.PAST;
        long from = 0;
        int size = 1;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        Mockito.lenient().when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(entity.getBooker()));
        Mockito.lenient().when(bookingRepository.findByBookerAndEndIsBefore(
                Mockito.any(User.class),
                Mockito.any(LocalDateTime.class),
                Mockito.any(Pageable.class)))
                .thenReturn(List.of(entity));

        LocalDateTime minNow = LocalDateTime.now();
        assertEquals(List.of(expectedDto),
                bookingService.findByBooker(bookerId, searchState, from, size));
        LocalDateTime maxNow = LocalDateTime.now();

        Mockito.verify(bookingRepository).findByBookerAndEndIsBefore(
                Mockito.argThat(booker -> Objects.nonNull(booker) &&
                        Objects.equals(booker.getId(), entity.getBooker().getId())),
                Mockito.argThat(now -> Objects.nonNull(now) &&
                        !now.isBefore(minNow) && !now.isAfter(maxNow)),
                Mockito.eq(PageRequest.of(0, 1, sort)));
    }

    @Test
    void findByBookerWithSearchStateFutureAndPaginationAndThenReturnListOfDto() {
        TestBookingBuilder bookingBuilder = TestBookingBuilder.defaultBuilder();
        Booking entity = bookingBuilder.buildEntity();
        BookingDtoOut expectedDto = bookingBuilder.buildDtoOut();
        long bookerId = entity.getBooker().getId();
        BookingSearchState searchState = BookingSearchState.FUTURE;
        long from = 0;
        int size = 1;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        Mockito.lenient().when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(entity.getBooker()));
        Mockito.lenient().when(bookingRepository.findByBookerAndStartIsAfter(
                        Mockito.any(User.class),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(entity));

        LocalDateTime minNow = LocalDateTime.now();
        assertEquals(List.of(expectedDto),
                bookingService.findByBooker(bookerId, searchState, from, size));
        LocalDateTime maxNow = LocalDateTime.now();

        Mockito.verify(bookingRepository).findByBookerAndStartIsAfter(
                Mockito.argThat(booker -> Objects.nonNull(booker) &&
                        Objects.equals(booker.getId(), entity.getBooker().getId())),
                Mockito.argThat(now -> Objects.nonNull(now) &&
                        !now.isBefore(minNow) && !now.isAfter(maxNow)),
                Mockito.eq(PageRequest.of(0, 1, sort)));
    }

    @Test
    void findByBookerWithSearchStateCurrentAndPaginationAndThenReturnListOfDto() {
        TestBookingBuilder bookingBuilder = TestBookingBuilder.defaultBuilder();
        Booking entity = bookingBuilder.buildEntity();
        BookingDtoOut expectedDto = bookingBuilder.buildDtoOut();
        long bookerId = entity.getBooker().getId();
        BookingSearchState searchState = BookingSearchState.CURRENT;
        long from = 0;
        int size = 1;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        Mockito.lenient().when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(entity.getBooker()));
        Mockito.lenient().when(bookingRepository.findByBookerAndStartIsBeforeAndEndIsAfter(
                        Mockito.any(User.class),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(entity));

        LocalDateTime minNow = LocalDateTime.now();
        assertEquals(List.of(expectedDto),
                bookingService.findByBooker(bookerId, searchState, from, size));
        LocalDateTime maxNow = LocalDateTime.now();

        Mockito.verify(bookingRepository).findByBookerAndStartIsBeforeAndEndIsAfter(
                Mockito.argThat(booker -> Objects.nonNull(booker) &&
                        Objects.equals(booker.getId(), entity.getBooker().getId())),
                Mockito.argThat(now -> Objects.nonNull(now) &&
                        !now.isBefore(minNow) && !now.isAfter(maxNow)),
                Mockito.argThat(now -> Objects.nonNull(now) &&
                        !now.isBefore(minNow) && !now.isAfter(maxNow)),
                Mockito.eq(PageRequest.of(0, 1, sort)));
    }

    @Test
    void findByBookerWithSearchStateWaitingAndPaginationAndThenReturnListOfDto() {
        TestBookingBuilder bookingBuilder = TestBookingBuilder.defaultBuilder();
        Booking entity = bookingBuilder.buildEntity();
        BookingDtoOut expectedDto = bookingBuilder.buildDtoOut();
        long bookerId = entity.getBooker().getId();
        BookingSearchState searchState = BookingSearchState.WAITING;
        long from = 0;
        int size = 1;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        Mockito.lenient().when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(entity.getBooker()));
        Mockito.lenient().when(bookingRepository.findByBookerAndStatusIs(
                        Mockito.argThat(booker -> Objects.nonNull(booker) &&
                                Objects.equals(booker.getId(), entity.getBooker().getId())),
                        Mockito.eq(BookingStatus.WAITING),
                        Mockito.eq(PageRequest.of(0, 1, sort))))
                .thenReturn(List.of(entity));

        assertEquals(List.of(expectedDto),
                bookingService.findByBooker(bookerId, searchState, from, size));
    }

    @Test
    void findByBookerWithSearchStateRejectedAndPaginationAndThenReturnListOfDto() {
        TestBookingBuilder bookingBuilder = TestBookingBuilder.defaultBuilder();
        Booking entity = bookingBuilder.buildEntity();
        BookingDtoOut expectedDto = bookingBuilder.buildDtoOut();
        long bookerId = entity.getBooker().getId();
        BookingSearchState searchState = BookingSearchState.REJECTED;
        long from = 0;
        int size = 1;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        Mockito.lenient().when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(entity.getBooker()));
        Mockito.lenient().when(bookingRepository.findByBookerAndStatusIs(
                        Mockito.argThat(booker -> Objects.nonNull(booker) &&
                                Objects.equals(booker.getId(), entity.getBooker().getId())),
                        Mockito.eq(BookingStatus.REJECTED),
                        Mockito.eq(PageRequest.of(0, 1, sort))))
                .thenReturn(List.of(entity));

        assertEquals(List.of(expectedDto),
                bookingService.findByBooker(bookerId, searchState, from, size));
    }

    @Test
    void findByBookerWithSearchStateAllAndPaginationAndThenReturnListOfDto() {
        TestBookingBuilder bookingBuilder = TestBookingBuilder.defaultBuilder();
        Booking entity = bookingBuilder.buildEntity();
        BookingDtoOut expectedDto = bookingBuilder.buildDtoOut();
        long bookerId = entity.getBooker().getId();
        BookingSearchState searchState = BookingSearchState.ALL;
        long from = 0;
        int size = 1;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        Mockito.lenient().when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(entity.getBooker()));
        Mockito.lenient().when(bookingRepository.findByBooker(
                        Mockito.argThat(booker -> Objects.nonNull(booker) &&
                                Objects.equals(booker.getId(), entity.getBooker().getId())),
                        Mockito.eq(PageRequest.of(0, 1, sort))))
                .thenReturn(List.of(entity));

        assertEquals(List.of(expectedDto),
                bookingService.findByBooker(bookerId, searchState, from, size));
    }

    @Test
    void findByOwnerWithIncorrectBookerIdAndThenThrowNotFoundException() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> bookingService.findByBooker(1L, BookingSearchState.ALL, 0, 1));
    }

    @Test
    void findByOwnerWithSearchStatePastAndPaginationAndThenReturnListOfDto() {
        TestBookingBuilder bookingBuilder = TestBookingBuilder.defaultBuilder();
        Booking entity = bookingBuilder.buildEntity();
        BookingDtoOut expectedDto = bookingBuilder.buildDtoOut();
        User owner = entity.getItem().getOwner();
        long ownerId = owner.getId();
        BookingSearchState searchState = BookingSearchState.PAST;
        long from = 0;
        int size = 1;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        Mockito.lenient().when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        Mockito.lenient().when(bookingRepository.findByItemOwnerAndEndIsBefore(
                        Mockito.any(User.class),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(entity));

        LocalDateTime minNow = LocalDateTime.now();
        assertEquals(List.of(expectedDto),
                bookingService.findByOwner(ownerId, searchState, from, size));
        LocalDateTime maxNow = LocalDateTime.now();

        Mockito.verify(bookingRepository).findByItemOwnerAndEndIsBefore(
                Mockito.argThat(user -> Objects.nonNull(user) &&
                        Objects.equals(user.getId(), owner.getId())),
                Mockito.argThat(now -> Objects.nonNull(now) &&
                        !now.isBefore(minNow) && !now.isAfter(maxNow)),
                Mockito.eq(PageRequest.of(0, 1, sort)));
    }

    @Test
    void findByOwnerWithSearchStateFutureAndPaginationAndThenReturnListOfDto() {
        TestBookingBuilder bookingBuilder = TestBookingBuilder.defaultBuilder();
        Booking entity = bookingBuilder.buildEntity();
        BookingDtoOut expectedDto = bookingBuilder.buildDtoOut();
        User owner = entity.getItem().getOwner();
        long ownerId = owner.getId();
        BookingSearchState searchState = BookingSearchState.FUTURE;
        long from = 0;
        int size = 1;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        Mockito.lenient().when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        Mockito.lenient().when(bookingRepository.findByItemOwnerAndStartIsAfter(
                        Mockito.any(User.class),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(entity));

        LocalDateTime minNow = LocalDateTime.now();
        assertEquals(List.of(expectedDto),
                bookingService.findByOwner(ownerId, searchState, from, size));
        LocalDateTime maxNow = LocalDateTime.now();

        Mockito.verify(bookingRepository).findByItemOwnerAndStartIsAfter(
                Mockito.argThat(user -> Objects.nonNull(user) &&
                        Objects.equals(user.getId(), owner.getId())),
                Mockito.argThat(now -> Objects.nonNull(now) &&
                        !now.isBefore(minNow) && !now.isAfter(maxNow)),
                Mockito.eq(PageRequest.of(0, 1, sort)));
    }

    @Test
    void findByOwnerWithSearchStateCurrentAndPaginationAndThenReturnListOfDto() {
        TestBookingBuilder bookingBuilder = TestBookingBuilder.defaultBuilder();
        Booking entity = bookingBuilder.buildEntity();
        BookingDtoOut expectedDto = bookingBuilder.buildDtoOut();
        User owner = entity.getItem().getOwner();
        long ownerId = owner.getId();
        BookingSearchState searchState = BookingSearchState.CURRENT;
        long from = 0;
        int size = 1;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        Mockito.lenient().when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        Mockito.lenient().when(bookingRepository.findByItemOwnerAndStartIsBeforeAndEndIsAfter(
                        Mockito.any(User.class),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(entity));

        LocalDateTime minNow = LocalDateTime.now();
        assertEquals(List.of(expectedDto),
                bookingService.findByOwner(ownerId, searchState, from, size));
        LocalDateTime maxNow = LocalDateTime.now();

        Mockito.verify(bookingRepository).findByItemOwnerAndStartIsBeforeAndEndIsAfter(
                Mockito.argThat(user -> Objects.nonNull(user) &&
                        Objects.equals(user.getId(), owner.getId())),
                Mockito.argThat(now -> Objects.nonNull(now) &&
                        !now.isBefore(minNow) && !now.isAfter(maxNow)),
                Mockito.argThat(now -> Objects.nonNull(now) &&
                        !now.isBefore(minNow) && !now.isAfter(maxNow)),
                Mockito.eq(PageRequest.of(0, 1, sort)));
    }

    @Test
    void findByOwnerWithSearchStateWaitingAndPaginationAndThenReturnListOfDto() {
        TestBookingBuilder bookingBuilder = TestBookingBuilder.defaultBuilder();
        Booking entity = bookingBuilder.buildEntity();
        BookingDtoOut expectedDto = bookingBuilder.buildDtoOut();
        User owner = entity.getItem().getOwner();
        long ownerId = owner.getId();
        BookingSearchState searchState = BookingSearchState.WAITING;
        long from = 0;
        int size = 1;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        Mockito.lenient().when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        Mockito.lenient().when(bookingRepository.findByItemOwnerAndStatusIs(
                        Mockito.argThat(user -> Objects.nonNull(user) &&
                                Objects.equals(user.getId(), owner.getId())),
                        Mockito.eq(BookingStatus.WAITING),
                        Mockito.eq(PageRequest.of(0, 1, sort))))
                .thenReturn(List.of(entity));

        assertEquals(List.of(expectedDto),
                bookingService.findByOwner(ownerId, searchState, from, size));
    }

    @Test
    void findByOwnerWithSearchStateRejectedAndPaginationAndThenReturnListOfDto() {
        TestBookingBuilder bookingBuilder = TestBookingBuilder.defaultBuilder();
        Booking entity = bookingBuilder.buildEntity();
        BookingDtoOut expectedDto = bookingBuilder.buildDtoOut();
        User owner = entity.getItem().getOwner();
        long ownerId = owner.getId();
        BookingSearchState searchState = BookingSearchState.REJECTED;
        long from = 0;
        int size = 1;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        Mockito.lenient().when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        Mockito.lenient().when(bookingRepository.findByItemOwnerAndStatusIs(
                        Mockito.argThat(user -> Objects.nonNull(user) &&
                                Objects.equals(user.getId(), owner.getId())),
                        Mockito.eq(BookingStatus.REJECTED),
                        Mockito.eq(PageRequest.of(0, 1, sort))))
                .thenReturn(List.of(entity));

        assertEquals(List.of(expectedDto),
                bookingService.findByOwner(ownerId, searchState, from, size));
    }

    @Test
    void findByOwnerWithSearchStateAllAndPaginationAndThenReturnListOfDto() {
        TestBookingBuilder bookingBuilder = TestBookingBuilder.defaultBuilder();
        Booking entity = bookingBuilder.buildEntity();
        BookingDtoOut expectedDto = bookingBuilder.buildDtoOut();
        User owner = entity.getItem().getOwner();
        long ownerId = owner.getId();
        BookingSearchState searchState = BookingSearchState.ALL;
        long from = 0;
        int size = 1;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        Mockito.lenient().when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        Mockito.lenient().when(bookingRepository.findByItemOwner(
                        Mockito.argThat(user -> Objects.nonNull(user) &&
                                Objects.equals(user.getId(), owner.getId())),
                        Mockito.eq(PageRequest.of(0, 1, sort))))
                .thenReturn(List.of(entity));

        assertEquals(List.of(expectedDto),
                bookingService.findByOwner(ownerId, searchState, from, size));
    }
}