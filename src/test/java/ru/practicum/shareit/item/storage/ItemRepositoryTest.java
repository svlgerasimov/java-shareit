package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.testutils.TestEntityBuilders.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    TestEntityManager em;

    @Test
    void search() {
        User user1 = TestUserBuilder.defaultBuilder().buildEntity();
        em.persistAndGetId(user1);

        TestItemBuilder itemBuilder = TestItemBuilder.defaultBuilder().owner(user1);
        Item item1 = itemBuilder.name("name").description("description").buildEntity();
        Item item2 = itemBuilder.name("HereIsTeXtToFind").description("description").buildEntity();
        Item item3 = itemBuilder.name("name").description("Here id TeXt to find").buildEntity();
        em.persistAndGetId(item1);
        em.persistAndGetId(item2);
        em.persistAndGetId(item3);

        Set<Long> idsFromQuery = itemRepository.search("text", Pageable.unpaged())
                .stream()
                .map(Item::getId)
                .collect(Collectors.toSet());

        assertEquals(Set.of(item2.getId(), item3.getId()), idsFromQuery);
    }
}