package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.testutils.TestEntityBuilders.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    TestEntityManager em;



    @Test
    void findByIdAndItemOwnerIdOrBookerId() {
        TestUserBuilder userBuilder = TestUserBuilder.defaultBuilder();
        User user1 = userBuilder.email("email1@mail.com").buildEntity();
        User user2 = userBuilder.email("email2@mail.com").buildEntity();
        User user3 = userBuilder.email("email3@mail.com").buildEntity();
        User user4 = userBuilder.email("email4@mail.com").buildEntity();
        em.persistAndGetId(user1);
        em.persistAndGetId(user2);
        em.persistAndGetId(user3);
        em.persistAndGetId(user4);
        TestItemBuilder itemBuilder = TestItemBuilder.defaultBuilder();
        Item item1 = itemBuilder.owner(user1).buildEntity();
        Item item2 = itemBuilder.owner(user2).buildEntity();
        em.persistAndGetId(item1);
        em.persistAndGetId(item2);
        TestBookingBuilder bookingBuilder = TestBookingBuilder.defaultBuilder();
        Booking booking1 = bookingBuilder.item(item1).booker(user3).buildEntity();
        Booking booking2 = bookingBuilder.item(item2).booker(user4).buildEntity();
        em.persistAndGetId(booking1);
        em.persistAndGetId(booking2);

        //owner
        Optional<Booking> queryResult =
                bookingRepository.findByIdAndItemOwnerIdOrBookerId(booking1.getId(), user1.getId());
        assertTrue(queryResult.isPresent());
        assertEquals(booking1.getId(), queryResult.get().getId());

        //booker
        queryResult =
                bookingRepository.findByIdAndItemOwnerIdOrBookerId(booking1.getId(), user3.getId());
        assertTrue(queryResult.isPresent());
        assertEquals(booking1.getId(), queryResult.get().getId());

        //neither owner nor booker
        queryResult =
                bookingRepository.findByIdAndItemOwnerIdOrBookerId(booking1.getId(), user2.getId());
        assertTrue(queryResult.isEmpty());
    }
}