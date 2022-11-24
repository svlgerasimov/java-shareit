package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.CustomValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.Validator;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
//@Validated
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) {
        return userService.getById(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @PostMapping
//    @Validated(UserDto.ValidatedFull.class)
//    public UserDto add(@RequestBody @Valid UserDto userDto) {
    public UserDto add(@RequestBody @Valid UserDto userDto) {
        fullValidateUserDto(userDto);
        return userService.add(userDto);
    }

    @PatchMapping("/{id}")
//    public UserDto patch(@PathVariable long id,
//                         @RequestBody @Validated(UserDto.ValidatedPatch.class) UserDto patchDto) {
    public UserDto patch(@PathVariable long id,
                         @RequestBody @Valid UserDto patchDto) {
        return userService.patch(id, patchDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        userService.remove(id);
    }

//    @Validated
//    private UserDto update(@Valid UserDto userDto) {
//        return userService.update(userDto);
//    }

    private void fullValidateUserDto(UserDto userDto) {
        if (!userDto.hasEmail()) {
            throw new CustomValidationException("No 'email' field");
        }
    }

}
