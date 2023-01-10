package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Spy
    private static UserDtoMapper userDtoMapper = new UserDtoMapperImpl();
    @Spy
    private static UserPatchDtoMapper userPatchDtoMapper = new UserPatchDtoMapperImpl();


    @NoArgsConstructor(staticName = "defaultBuilder")
    @AllArgsConstructor(staticName = "all")
    @Setter
    @Accessors(chain = true, fluent = true)
    private static class TestUserBuilder {
        private Long id = 1L;
        private String name = "name";
        private String email = "email@mail.com";

        public User buildEntity() {
            User user = new User();
            user.setId(id);
            user.setName(name);
            user.setEmail(email);
            return user;
        }

        public UserDto buildDto() {
            return new UserDto(id, name, email);
        }

        public UserPatchDto buildPatchDto() {
            return new UserPatchDto(name, email);
        }
    }

    @Test
    void addCorrectDtoAndThenReturnSavedDto() {
        TestUserBuilder userBuilder = TestUserBuilder.defaultBuilder();
        userBuilder.id(0L);
        UserDto inputDto = userBuilder.buildDto();
        userBuilder.id(null);
        User inputEntity = userBuilder.buildEntity();
        userBuilder.id(1L);
        UserDto outputDto = userBuilder.buildDto();
        User outputEntity = userBuilder.buildEntity();

        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(outputEntity);

        UserDto actualDto = userService.add(inputDto);

        Mockito.verify(userRepository)
                .save(Mockito.argThat(user -> Objects.nonNull(user) &&
                        Objects.equals(user.getId(), inputEntity.getId()) &&
                        Objects.equals(user.getName(), inputEntity.getName()) &&
                        Objects.equals(user.getEmail(), inputEntity.getEmail())));

        assertEquals(outputDto, actualDto);
    }

    @Test
    void patchWithAbsentUserAndThenThrowNotFoundException() {
        long id = 1L;
        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.patch(id, TestUserBuilder.defaultBuilder().buildPatchDto()));

        Mockito.verify(userRepository).findById(id);
    }

    @Test
    void patchWithNameAndThenReturnPatchedDto() {
        TestUserBuilder userBuilder = TestUserBuilder.defaultBuilder();
        User initialEntity = userBuilder.buildEntity();
        long id = initialEntity.getId();
        String patchName = initialEntity.getName() + " updated";
        UserPatchDto inputDto = new UserPatchDto(patchName, null);
        userBuilder.name(patchName);
        UserDto patchedDto = userBuilder.buildDto();

        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.of(initialEntity));

        assertEquals(patchedDto, userService.patch(id, inputDto));
    }

    @Test
    void patchWithEmailAndThenReturnPatchedDto() {
        TestUserBuilder userBuilder = TestUserBuilder.defaultBuilder();
        User initialEntity = userBuilder.buildEntity();
        long id = initialEntity.getId();
        String patchEmail = initialEntity.getEmail() + " updated";
        UserPatchDto inputDto = new UserPatchDto(null, patchEmail);
        userBuilder.email(patchEmail);
        UserDto patchedDto = userBuilder.buildDto();

        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.of(initialEntity));

        assertEquals(patchedDto, userService.patch(id, inputDto));
    }

    @Test
    void removeWithCorrectIdAndThenNotThrowException() {
        long id = 1;
        assertDoesNotThrow(() -> userService.remove(id));
        Mockito.verify(userRepository).deleteById(id);
    }

    @Test
    void removeWithAbsentIdAndThenThrowNotFoundException() {
        long id = 1;
        Mockito.doThrow(EmptyResultDataAccessException.class)
                .when(userRepository).deleteById(id);

        assertThrows(NotFoundException.class, () -> userService.remove(id));

        Mockito.verify(userRepository).deleteById(id);
    }

    @Test
    void getAllWithNoUsersAndThenReturnEmptyList() {
        Mockito.when(userRepository.findAll())
                .thenReturn(Collections.emptyList());

        assertEquals(Collections.emptyList(), userService.getAll());

        Mockito.verify(userRepository).findAll();
    }

    @Test
    void getAllWithExistingUsersAndThenReturnListOfDto() {
        TestUserBuilder userBuilder = TestUserBuilder.all(1L, "name1", "email1@mail.com");
        User entity1 = userBuilder.buildEntity();
        UserDto dto1 = userBuilder.buildDto();
        userBuilder.id(2L).name("name2").email("email2@mail.com");
        User entity2 = userBuilder.buildEntity();
        UserDto dto2 = userBuilder.buildDto();
        List<User> entities = List.of(entity1, entity2);
        List<UserDto> dtos = List.of(dto1, dto2);

        Mockito.when(userRepository.findAll()).thenReturn(entities);

        assertEquals(dtos, userService.getAll());
    }

    @Test
    void getByIdWithAbsentIdAndThenThrowNotFoundException() {
        long id = 1L;
        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(id));
    }

    @Test
    void getByIdWithExistingIdAndThenReturnDto() {
        TestUserBuilder userBuilder = TestUserBuilder.defaultBuilder();
        User entity = userBuilder.buildEntity();
        UserDto dto = userBuilder.buildDto();
        long id = entity.getId();

        Mockito.when(userRepository.findById(id))
                .thenReturn(Optional.of(entity));

        assertEquals(dto, userService.getById(id));
    }
}