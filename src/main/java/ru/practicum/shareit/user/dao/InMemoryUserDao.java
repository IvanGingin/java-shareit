package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class InMemoryUserDao implements UserDao {
    private final Map<Long, User> users = new HashMap<>();
    private long userIdGenerator = 0;

    @Override
    public User addUser(UserDto userDto) {
        log.debug("Добавление пользователя с данными: {}", userDto);
        try {
            // Проверка уникальности email
            for (User existingUser : users.values()) {
                if (existingUser.getEmail().equals(userDto.getEmail())) {
                    log.error("Конфликт: пользователь с такой почтой уже существует: {}", userDto.getEmail());
                    throw new ConflictException("Пользователь с такой почтой уже существует");
                }
            }
            long userId = ++userIdGenerator;
            User user = UserMapper.toModel(userDto);
            user.setId(userId);
            users.put(userId, user);
            log.debug("Пользователь успешно добавлен с id={}", userId);
            return user;
        } catch (Exception e) {
            log.error("Ошибка при добавлении пользователя: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public User updateUser(Long userId, UserUpdateDto userUpdateDto) {
        log.debug("Обновление пользователя с id={}, данными: {}", userId, userUpdateDto);
        try {
            User existingUser = users.get(userId);
            if (existingUser == null) {
                log.error("Пользователь с id={} не найден!", userId);
                throw new NotFoundException("Пользователь с id=" + userId + " не найден!");
            }
            if (userUpdateDto.getEmail() != null && !userUpdateDto.getEmail().isBlank()) {
                for (User user : users.values()) {
                    if (!user.getId().equals(userId) && user.getEmail().equals(userUpdateDto.getEmail())) {
                        log.error("Конфликт: пользователь с такой почтой уже существует: {}", userUpdateDto.getEmail());
                        throw new ConflictException("Пользователь с такой электронной почтой уже существует!");
                    }
                }
                existingUser.setEmail(userUpdateDto.getEmail());
            }
            if (userUpdateDto.getName() != null && !userUpdateDto.getName().isBlank()) {
                existingUser.setName(userUpdateDto.getName());
            }

            users.put(userId, existingUser);
            log.debug("Пользователь с id={} успешно обновлён: {}", userId, existingUser);
            return existingUser;
        } catch (Exception e) {
            log.error("Ошибка при обновлении пользователя с id={}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public User getUser(Long userId) {
        log.debug("Получение пользователя с id={}", userId);
        try {
            User user = users.get(userId);
            if (user == null) {
                log.error("Пользователь с id={} не найден!", userId);
                throw new NotFoundException("Пользователь с id=" + userId + " не найден!");
            }
            log.debug("Пользователь с id={} найден: {}", userId, user);
            return user;
        } catch (Exception e) {
            log.error("Ошибка при получении пользователя с id={}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<User> getUsers() {
        log.debug("Получение всех пользователей");
        try {
            List<User> userList = new ArrayList<>(users.values());
            log.debug("Всего пользователей найдено: {}", userList.size());
            return userList;
        } catch (Exception e) {
            log.error("Ошибка при получении списка пользователей: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteUser(Long userId) {
        log.debug("Удаление пользователя с id={}", userId);
        try {
            users.remove(userId);
            log.debug("Пользователь с id={} успешно удалён", userId);
        } catch (Exception e) {
            log.error("Ошибка при удалении пользователя с id={}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }
}
