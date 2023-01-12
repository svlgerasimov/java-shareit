package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.testutils.TestEntityBuilders.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class UserServiceImplIntegrationTest {

    private final UserService userService;
    private final EntityManager em;

    @Test
    void add() {
        UserDto userDto = new UserDto(null, "name", "email@mail.com");

        userService.add(userDto);

        List<User> users = em.createQuery("select u from User u", User.class)
                .getResultList();

        assertThat(users.size()).isEqualTo(1);
        User user = users.get(0);
        assertThat(user.getId()).isNotNull();
        assertThat(user.getName()).isEqualTo("name");
        assertThat(user.getEmail()).isEqualTo("email@mail.com");
    }

    @Test
    void patch() {
        UserPatchDto patchDto = new UserPatchDto("patchedName", "patchedemail@mail.com");

        User user = TestUserBuilder.all(null, "name", "mail@mail.com").buildEntity();

        em.persist(user);

        userService.patch(user.getId(), patchDto);

        List<User> users = em.createQuery("select u from User u", User.class)
                .getResultList();

        assertThat(users.size()).isEqualTo(1);
        User patchedUser = users.get(0);
        assertThat(patchedUser.getId()).isEqualTo(user.getId());
        assertThat(patchedUser.getName()).isEqualTo(patchDto.getName());
        assertThat(patchedUser.getEmail()).isEqualTo(patchDto.getEmail());
    }

    @Test
    void remove() {
        User user = TestUserBuilder.defaultBuilder().id(null).buildEntity();
        em.persist(user);

        userService.remove(user.getId());

        List<User> users = em.createQuery("select u from User u", User.class)
                .getResultList();

        assertThat(users.isEmpty()).isTrue();
    }

    @Test
    void getAll() {
        List<User> sourceUsers = List.of(
                TestUserBuilder.all(null, "name1", "mail1@mail.com").buildEntity(),
                TestUserBuilder.all(null, "name2", "mail2@mail.com").buildEntity(),
                TestUserBuilder.all(null, "name3", "mail3@mail.com").buildEntity()
        );

        sourceUsers.forEach(em::persist);

        List<UserDto> userDtos = userService.getAll();

        assertThat(userDtos).hasSize(3);

        List<UserDto> expectedDtos = List.of(
                new UserDto(sourceUsers.get(0).getId(), "name1", "mail1@mail.com"),
                new UserDto(sourceUsers.get(1).getId(), "name2", "mail2@mail.com"),
                new UserDto(sourceUsers.get(2).getId(), "name3", "mail3@mail.com")
        );

        assertThat(userDtos).containsAll(expectedDtos);
    }

    @Test
    void getById() {
        List<User> sourceUsers = List.of(
                TestUserBuilder.all(null, "name1", "mail1@mail.com").buildEntity(),
                TestUserBuilder.all(null, "name2", "mail2@mail.com").buildEntity(),
                TestUserBuilder.all(null, "name3", "mail3@mail.com").buildEntity()
        );

        sourceUsers.forEach(em::persist);

        Long id = sourceUsers.get(1).getId();

        UserDto dto = userService.getById(id);

        assertThat(dto).isEqualTo(new UserDto(id, "name2", "mail2@mail.com"));
    }
}