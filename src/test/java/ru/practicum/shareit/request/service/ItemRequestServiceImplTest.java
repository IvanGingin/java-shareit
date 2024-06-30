package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Иван Иванов", "ivan.ivanov@example.com");
        itemRequest = new ItemRequest(1L, "Описание запроса 1", user, LocalDateTime.now());
        itemRequestDto = new ItemRequestDto(1L, "Описание запроса 1", LocalDateTime.now(), 1L, new ArrayList<>());
        item = new Item(1L, "Предмет 1", "Описание предмета 1", true, user, itemRequest);
        itemDto = new ItemDto(1L, "Предмет 1", "Описание предмета 1", true, 1L, 1L, null, null, new ArrayList<>());
    }

    @Test
    void addRequest_Valid() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestMapper.toModel(any(ItemRequestDto.class), any(User.class))).thenReturn(itemRequest);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(itemRequestMapper.toItemRequestDto(any(ItemRequest.class), anyList())).thenReturn(itemRequestDto);
        ItemRequestDto result = itemRequestService.addRequest(1L, itemRequestDto);
        assertNotNull(result);
        assertEquals(itemRequestDto, result);
        verify(userRepository, times(1)).findById(1L);
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void addRequest_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemRequestService.addRequest(1L, itemRequestDto);
        });
        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void addRequest_NullRequestDto() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            itemRequestService.addRequest(1L, null);
        });
        assertEquals("Request cannot be null", exception.getMessage());
    }

    @Test
    void getRequest_Valid() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item));
        when(itemRequestMapper.toItemRequestDto(any(ItemRequest.class), anyList())).thenReturn(itemRequestDto);
        ItemRequestDto result = itemRequestService.getRequest(1L, 1L);
        assertNotNull(result);
        assertEquals(itemRequestDto, result);
        verify(itemRequestRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).findByRequestId(1L);
    }

    @Test
    void getRequest_UserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemRequestService.getRequest(1L, 1L);
        });
        assertEquals("Пользователь не существует!", exception.getMessage());
        verify(userRepository, times(1)).existsById(1L);
    }

    @Test
    void getRequest_RequestNotFound() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemRequestService.getRequest(1L, 1L);
        });
        assertEquals("Запрос не найден!", exception.getMessage());
        verify(itemRequestRepository, times(1)).findById(1L);
    }

    @Test
    void getUserRequests_Valid() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorId(1L)).thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item));
        when(itemRequestMapper.toItemRequestDto(any(ItemRequest.class), anyList())).thenReturn(itemRequestDto);
        List<ItemRequestDto> result = itemRequestService.getUserRequests(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequestDto, result.get(0));
        verify(itemRequestRepository, times(1)).findAllByRequestorId(1L);
        verify(itemRepository, times(1)).findByRequestId(1L);
    }

    @Test
    void getUserRequests_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemRequestService.getUserRequests(1L);
        });
        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getAllRequests_Valid() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findAmountOfRequests(1L)).thenReturn(10);
        when(itemRequestRepository.findAllInPage(eq(1L), any(PageRequest.class))).thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item));
        when(itemRequestMapper.toItemRequestDto(any(ItemRequest.class), anyList())).thenReturn(itemRequestDto);
        List<ItemRequestDto> result = itemRequestService.getAllRequests(1L, 0, 10);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequestDto, result.get(0));
        verify(itemRequestRepository, times(1)).findAllInPage(eq(1L), any(PageRequest.class));
        verify(itemRepository, times(1)).findByRequestId(1L);
    }

    @Test
    void getAllRequests_UserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemRequestService.getAllRequests(1L, 0, 10);
        });
        assertEquals("Пользователь не найден!", exception.getMessage());
        verify(userRepository, times(1)).existsById(1L);
    }
}
