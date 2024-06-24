package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto addUser(@Valid UserDto userDto) {
        log.debug("Добавление пользователя с данными: {}", userDto);
        User user = UserMapper.toModel(userDto);
        User savedUser = userRepository.save(user);
        log.debug("Пользователь успешно добавлен: {}", savedUser);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserUpdateDto userUpdateDto) {
        log.debug("Обновление пользователя с id={}, данными: {}", userId, userUpdateDto);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден!");
        }
        User existingUser = optionalUser.get();
        if (userUpdateDto.getEmail() != null && !userUpdateDto.getEmail().isBlank()) {
            existingUser.setEmail(userUpdateDto.getEmail());
        }
        if (userUpdateDto.getName() != null && !userUpdateDto.getName().isBlank()) {
            existingUser.setName(userUpdateDto.getName());
        }
        User updatedUser = userRepository.save(existingUser);
        log.debug("Пользователь с id={} успешно обновлён: {}", userId, updatedUser);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public UserDto getUser(Long userId) {
        log.debug("Получение пользователя с id={}", userId);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден!");
        }
        User user = optionalUser.get();
        log.debug("Пользователь с id={} найден: {}", userId, user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getUsers() {
        log.debug("Получение всех пользователей");
        List<User> users = userRepository.findAll();
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : users) {
            userDtoList.add(UserMapper.toUserDto(user));
        }
        return userDtoList;
    }

    @Override
    public void deleteUser(Long userId) {
        log.debug("Удаление пользователя с id={}", userId);
        userRepository.deleteById(userId);
        log.debug("Пользователь с id={} успешно удалён", userId);
    }
}
