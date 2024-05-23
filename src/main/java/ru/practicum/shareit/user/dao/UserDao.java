package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {
    User addUser(UserDto userDto);

    User updateUser(Long userId, UserDto userDto);

    User getUser(Long userId);

    List<User> getUsers();

    void deleteUser(Long userId);
}

