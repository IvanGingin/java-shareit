package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto updateUser(Long userId, UserUpdateDto userUpdateDto);

    UserDto getUser(Long userId);

    List<UserDto> getUsers();

    void deleteUser(Long userId);
}
