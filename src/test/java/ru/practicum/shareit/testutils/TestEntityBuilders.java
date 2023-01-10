package ru.practicum.shareit.testutils;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class TestEntityBuilders {
    @NoArgsConstructor(staticName = "defaultBuilder")
    @AllArgsConstructor(staticName = "all")
    @Setter
    @Accessors(chain = true, fluent = true)
    public static class TestUserBuilder {
        private Long id;
        private String name = "name";
        private String email = "email@mail.com";

        public User buildEntity() {
            User user = new User();
            user.setId(id);
            user.setName(name);
            user.setEmail(email);
            return user;
        }
    }

    @NoArgsConstructor(staticName = "defaultBuilder")
    @AllArgsConstructor(staticName = "all")
    @Setter
    @Accessors(chain = true, fluent = true)
    public static class TestItemBuilder {
        private Long id;
        private String name = "name";
        private String description = "description";
        private Boolean available = true;
        private User owner;
        private ItemRequest request;

        public Item buildEntity() {
            Item item = new Item();
            item.setId(id);
            item.setName(name);
            item.setDescription(description);
            item.setAvailable(available);
            item.setOwner(owner);
            item.setRequest(request);
            return item;
        }
    }

    @NoArgsConstructor(staticName = "defaultBuilder")
    @AllArgsConstructor(staticName = "all")
    @Setter
    @Accessors(chain = true, fluent = true)
    public static class TestBookingBuilder {
        private Long id;
        private LocalDateTime start = LocalDateTime.now().plusDays(2);
        private LocalDateTime end = LocalDateTime.now().plusDays(4);
        private Item item;
        private User booker;
        private BookingStatus status = BookingStatus.WAITING;

        public Booking buildEntity() {
            Booking booking = new Booking();
            booking.setId(id);
            booking.setStart(start);
            booking.setEnd(end);
            booking.setItem(item);
            booking.setBooker(booker);
            booking.setStatus(status);
            return booking;
        }
    }
}
