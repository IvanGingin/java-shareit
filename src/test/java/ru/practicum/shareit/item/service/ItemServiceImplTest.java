package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private ItemDto itemDto;
    private ItemUpdateDto itemUpdateDto;
    private Validator validator;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Test User", "testuser@example.com");
        item = new Item(1L, "Item Name", "Item Description", true, user, null);
        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item Name");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);
        itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setName("Updated Item Name");
        itemUpdateDto.setDescription("Updated Item Description");
        itemUpdateDto.setAvailable(false);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testAddItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        ItemDto result = itemService.addItem(1L, itemDto);
        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testAddItem_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemService.addItem(1L, itemDto);
        });
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void testAddItem_NullInput() {
        Exception exception = assertThrows(ValidationException.class, () -> {
            itemService.addItem(1L, null);
        });
        assertEquals("ItemDto cannot be null", exception.getMessage());
    }

    @Test
    void testAddItem_WithoutName() {
        itemDto.setName(null);
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        ConstraintViolation<ItemDto> violation = violations.stream().filter(v -> v.getPropertyPath().toString().equals("name")).findFirst().orElse(null);
        assertNotNull(violation);
        assertEquals("Название вещи отсутствует", violation.getMessage());
    }

    @Test
    void testAddItem_WithoutDescription() {
        itemDto.setDescription(null);
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        ConstraintViolation<ItemDto> violation = violations.stream().filter(v -> v.getPropertyPath().toString().equals("description")).findFirst().orElse(null);
        assertNotNull(violation);
        assertEquals("Описание вещи отсутствует", violation.getMessage());
    }

    @Test
    void testAddItem_WithoutStatus() {
        itemDto.setAvailable(null);
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        ConstraintViolation<ItemDto> violation = violations.stream().filter(v -> v.getPropertyPath().toString().equals("available")).findFirst().orElse(null);
        assertNotNull(violation);
        assertEquals("Статус вещи отсутствует", violation.getMessage());
    }

    @Test
    void testUpdateItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        ItemDto result = itemService.updateItem(1L, 1L, itemUpdateDto);
        assertNotNull(result);
        assertEquals(itemUpdateDto.getName(), result.getName());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testUpdateItem_ItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(1L, 1L, itemUpdateDto);
        });
        assertEquals("Вещь не найдена", exception.getMessage());
    }

    @Test
    void testUpdateItem_UserNotOwner() {
        User anotherUser = new User(2L, "Another User", "anotheruser@example.com");
        item.setOwner(anotherUser);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Exception exception = assertThrows(ForbiddenException.class, () -> {
            itemService.updateItem(1L, 1L, itemUpdateDto);
        });
        assertEquals("Пользователь не является владельцем вещи", exception.getMessage());
    }

    @Test
    void testGetItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(anyLong())).thenReturn(List.of());
        ItemDto result = itemService.getItem(1L, 1L);
        assertNotNull(result);
        assertEquals(item.getName(), result.getName());
    }

    @Test
    void testGetItem_NotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemService.getItem(1L, 1L);
        });
        assertEquals("Вещь не найдена", exception.getMessage());
    }

    @Test
    void testGetItems() {
        List<Item> items = List.of(item);
        when(itemRepository.findByOwnerId(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(items));
        List<ItemDto> result = itemService.getItems(1L, 0, 10);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(item.getName(), result.get(0).getName());
    }

    @Test
    void testGetItems_Empty() {
        when(itemRepository.findByOwnerId(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));
        List<ItemDto> result = itemService.getItems(1L, 0, 10);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchItems() {
        List<Item> items = List.of(item);
        when(itemRepository.search(anyString(), any(Pageable.class))).thenReturn(new PageImpl<>(items));
        List<ItemDto> result = itemService.searchItems("Item", 0, 10);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(item.getName(), result.get(0).getName());
    }

    @Test
    void testSearchItems_Empty() {
        when(itemRepository.search(anyString(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));
        List<ItemDto> result = itemService.searchItems("Item", 0, 10);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testDeleteItem() {
        doNothing().when(itemRepository).deleteById(anyLong());
        itemService.deleteItem(1L);
        verify(itemRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void testAddComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test Comment");
        Comment comment = new Comment();
        comment.setText("Test Comment");
        comment.setItem(item);
        comment.setAuthor(user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(BookingStatus.class), any(LocalDateTime.class)))
                .thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        CommentDto result = itemService.addComment(1L, 1L, commentDto);
        assertNotNull(result);
        assertEquals(commentDto.getText(), result.getText());
    }

    @Test
    void testAddComment_ItemNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> {
            itemService.addComment(1L, 1L, new CommentDto());
        });
        assertEquals("Вещь не найдена", exception.getMessage());
    }

    @Test
    void testAddComment_UserNotBooked() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(BookingStatus.class), any(LocalDateTime.class)))
                .thenReturn(false);
        Exception exception = assertThrows(ValidationException.class, () -> {
            itemService.addComment(1L, 1L, new CommentDto());
        });
        assertEquals("Пользователь не может оставлять комментарии, если он не бронировал этот предмет", exception.getMessage());
    }

    @Test
    void testAddComment_WithoutText() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("");
        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto);
        ConstraintViolation<CommentDto> violation = violations.stream().filter(v -> v.getPropertyPath().toString().equals("text")).findFirst().orElse(null);
        assertNotNull(violation);
        assertEquals("Комментарий не может быть пустым", violation.getMessage());
    }
}
