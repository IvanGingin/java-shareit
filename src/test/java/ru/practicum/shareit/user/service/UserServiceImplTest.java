package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private UserDto userDto1;
    private Validator validator;

    @BeforeEach
    void setUp() {
        user1 = new User(1L, "User One", "user1@example.com");
        userDto1 = new UserDto(1L, "User One", "user1@example.com");
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void addUser_Valid() {
        when(userRepository.save(any(User.class))).thenReturn(user1);
        UserDto result = userService.addUser(userDto1);
        assertNotNull(result);
        assertEquals(userDto1, result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void addUser_DuplicateEmail() {
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException(""));
        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            userService.addUser(userDto1);
        });
        assertEquals("Пользователь с таким email уже существует!", exception.getMessage());
    }

    @Test
    void addUser_NoEmail() {
        UserDto invalidUserDto = new UserDto(null, "Test User", null);
        Set<ConstraintViolation<UserDto>> violations = validator.validate(invalidUserDto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Email отсутствует")));
    }

    @Test
    void addUser_InvalidEmail() {
        UserDto invalidUserDto = new UserDto(null, "Test User", "invalid-email");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(invalidUserDto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Неверный формат email")));
    }

    @Test
    void updateUser() {
        UserUpdateDto updateDto = new UserUpdateDto(1L, "Updated User", "updated@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class))).thenReturn(user1);
        UserDto result = userService.updateUser(1L, updateDto);
        assertNotNull(result);
        assertEquals(updateDto.getName(), result.getName());
        assertEquals(updateDto.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_SameEmail() {
        UserUpdateDto updateDto = new UserUpdateDto(1L, "User One", "user1@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class))).thenReturn(user1);
        UserDto result = userService.updateUser(1L, updateDto);
        assertNotNull(result);
        assertEquals(updateDto.getName(), result.getName());
        assertEquals(updateDto.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_EmailExists() {
        UserUpdateDto updateDto = new UserUpdateDto(1L, "User One", "user2@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException(""));
        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            userService.updateUser(1L, updateDto);
        });
        assertEquals("Пользователь с таким email уже существует!", exception.getMessage());
    }

    @Test
    void updateUser_Null() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userService.updateUser(1L, null);
        });
        assertEquals("Вы не передали информацию о пользователе!", exception.getMessage());
    }

    @Test
    void updateUser_NotFound() {
        UserUpdateDto updateDto = new UserUpdateDto(1L, "User One", "user1@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.updateUser(1L, updateDto);
        });
        assertEquals("Пользователь с id=1 не найден!", exception.getMessage());
    }

    @Test
    void getUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        UserDto result = userService.getUser(1L);
        assertNotNull(result);
        assertEquals(userDto1, result);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUser_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.getUser(1L);
        });
        assertEquals("Пользователь с id=1 не найден!", exception.getMessage());
    }

    @Test
    void deleteUser() {
        doNothing().when(userRepository).deleteById(1L);
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void createUser_Generic() {
        User user = new User(null, "User", "user@example.com");
        UserDto userDto = new UserDto(null, "User", "user@example.com");
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto result = userService.addUser(userDto);
        assertNotNull(result);
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getUser_Generic() {
        User user = new User(1L, "User", "user@example.com");
        UserDto userDto = new UserDto(1L, "User", "user@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserDto result = userService.getUser(1L);
        assertNotNull(result);
        assertEquals(userDto, result);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void deleteUser_Generic() {
        doNothing().when(userRepository).deleteById(anyLong());
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void createUserAfterDelete() {
        User user = new User(1L, "User", "user@example.com");
        UserDto userDto = new UserDto(1L, "User", "user@example.com");
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto result = userService.addUser(userDto);
        assertNotNull(result);
        assertEquals(userDto, result);
        verify(userRepository, times(1)).save(any(User.class));
        doNothing().when(userRepository).deleteById(1L);
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(1L);
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto resultAfterDelete = userService.addUser(userDto);
        assertNotNull(resultAfterDelete);
        assertEquals(userDto, resultAfterDelete);
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void getAllUsers() {
        User user2 = new User(2L, "User Two", "user2@example.com");
        UserDto userDto2 = new UserDto(2L, "User Two", "user2@example.com");
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        List<UserDto> result = userService.getUsers();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(userDto1));
        assertTrue(result.contains(userDto2));
        verify(userRepository, times(1)).findAll();
    }
}
