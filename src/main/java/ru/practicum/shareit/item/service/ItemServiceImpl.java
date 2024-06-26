package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto addItem(Long userId, @Valid ItemDto itemDto) {
        log.debug("Добавление вещи пользователем с id={}, данными: {}", userId, itemDto);
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest request = itemDto.getRequest() != null ? itemRequestRepository.findById(itemDto.getRequest()).orElse(null) : null;
        Item item = ItemMapper.toModel(itemDto, owner, request);
        Item savedItem = itemRepository.save(item);
        log.debug("Вещь успешно добавлена: {}", savedItem);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, @Valid ItemUpdateDto itemUpdateDto) {
        log.debug("Обновление вещи с id={} пользователем с id={}, данными: {}", itemId, userId, itemUpdateDto);
        Item existingItem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }
        if (itemUpdateDto.getName() != null) {
            existingItem.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.getDescription() != null) {
            existingItem.setDescription(itemUpdateDto.getDescription());
        }
        if (itemUpdateDto.getAvailable() != null) {
            existingItem.setAvailable(itemUpdateDto.getAvailable());
        }
        Item updatedItem = itemRepository.save(existingItem);
        log.debug("Вещь с id={} успешно обновлена: {}", itemId, updatedItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItem(Long itemId, Long userId) {
        log.debug("Получение вещи с id={}", itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        List<Comment> comments = commentRepository.findByItemId(itemId);
        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            Booking lastBooking = bookingRepository.findFirstByItemIdAndStartLessThanEqualAndStatusOrderByEndDesc(itemId, now, BookingStatus.APPROVED);
            Booking nextBooking = bookingRepository.findFirstByItemIdAndStartGreaterThanEqualAndStatusOrderByStartAsc(itemId, now, BookingStatus.APPROVED);
            return ItemMapper.toItemDto(item, comments, lastBooking, nextBooking);
        } else {
            return ItemMapper.toItemDto(item, comments, null, null);
        }
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        log.debug("Получение всех вещей пользователя с id={}", userId);
        return itemRepository.findByOwnerId(userId).stream()
                .map(item -> {
                    List<Comment> comments = commentRepository.findByItemId(item.getId());
                    LocalDateTime now = LocalDateTime.now();
                    Booking lastBooking = bookingRepository.findFirstByItemIdAndStartLessThanEqualAndStatusOrderByEndDesc(item.getId(), now, BookingStatus.APPROVED);
                    Booking nextBooking = bookingRepository.findFirstByItemIdAndStartGreaterThanEqualAndStatusOrderByStartAsc(item.getId(), now, BookingStatus.APPROVED);
                    return ItemMapper.toItemDto(item, comments, lastBooking, nextBooking);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        log.debug("Поиск вещей с текстом: {}", text);
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long itemId) {
        log.debug("Удаление вещи с id={}", itemId);
        itemRepository.deleteById(itemId);
        log.debug("Вещь с id={} успешно удалена", itemId);
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, @Valid CommentDto commentDto) {
        log.debug("Добавление комментария пользователем с id={} к вещи с id={}, данными: {}", userId, itemId, commentDto);
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        boolean hasBooking = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());
        if (!hasBooking) {
            throw new ValidationException("Пользователь не может оставлять комментарии, если он не бронировал этот предмет");
        }
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment = commentRepository.save(comment);
        log.debug("Комментарий успешно добавлен: {}", comment);
        return CommentMapper.toCommentDto(comment);
    }
}
