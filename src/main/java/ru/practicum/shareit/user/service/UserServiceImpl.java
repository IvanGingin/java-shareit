package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserDao inMemoryUserDao;

    @Override
    public UserDto addUser(@Valid UserDto userDto) {
        log.debug("Добавление пользователя с данными: {}", userDto);
        try {
            UserDto createdUser = UserMapper.toUserDto(inMemoryUserDao.addUser(userDto));
            log.debug("Пользователь успешно добавлен: {}", createdUser);
            return createdUser;
        } catch (Exception e) {
            log.error("Ошибка при добавлении пользователя: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public UserDto updateUser(Long userId, UserUpdateDto userUpdateDto) {
        log.debug("Обновление пользователя с id={}, данными: {}", userId, userUpdateDto);
        try {
            User updatedUser = inMemoryUserDao.updateUser(userId, userUpdateDto);
            log.debug("Пользователь с id={} успешно обновлён: {}", userId, updatedUser);
            return UserMapper.toUserDto(updatedUser);
        } catch (Exception e) {
            log.error("Ошибка при обновлении пользователя с id={}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public UserDto getUser(Long userId) {
        log.debug("Получение пользователя с id={}", userId);
        try {
            UserDto userDto = UserMapper.toUserDto(inMemoryUserDao.getUser(userId));
            log.debug("Пользователь с id={} найден: {}", userId, userDto);
            return userDto;
        } catch (Exception e) {
            log.error("Ошибка при получении пользователя с id={}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<UserDto> getUsers() {
        log.debug("Получение всех пользователей");
        try {
            List<User> users = inMemoryUserDao.getUsers();
            List<UserDto> userDtoList = new ArrayList<>();
            for (User user : users) {
                userDtoList.add(UserMapper.toUserDto(user));
            }
            log.debug("Всего пользователей найдено: {}", userDtoList.size());
            return userDtoList;
        } catch (Exception e) {
            log.error("Ошибка при получении списка пользователей: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteUser(Long userId) {
        log.debug("Удаление пользователя с id={}", userId);
        try {
            inMemoryUserDao.deleteUser(userId);
            log.debug("Пользователь с id={} успешно удалён", userId);
        } catch (Exception e) {
            log.error("Ошибка при удалении пользователя с id={}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }
}
