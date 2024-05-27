package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userServiceImpl;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        log.debug("Получен запрос на добавление пользователя с данными: {}", userDto);
        return userServiceImpl.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @Valid @RequestBody UserUpdateDto userUpdateDto) {
        log.debug("Получен запрос на обновление пользователя с id={}, данными: {}", userId, userUpdateDto);
        return userServiceImpl.updateUser(userId, userUpdateDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.debug("Получен запрос на получение пользователя с id={}", userId);
        return userServiceImpl.getUser(userId);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        log.debug("Получен запрос на получение всех пользователей");
        return userServiceImpl.getUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.debug("Получен запрос на удаление пользователя с id={}", userId);
        userServiceImpl.deleteUser(userId);
    }
}
