package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
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
    @Transactional
    public ItemDto addItem(Long userId, @Valid ItemDto itemDto) {
        log.debug("Добавление вещи пользователем с id={}, данными: {}", userId, itemDto);
        if (userId == null) {
            throw new ValidationException("Идентификатор пользователя отсутствует");
        }
        if (itemDto == null) {
            throw new ValidationException("ItemDto cannot be null");
        }
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            throw new ValidationException("Название вещи отсутствует");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            throw new ValidationException("Описание вещи отсутствует");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Статус доступности вещи отсутствует");
        }
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest request = itemDto.getRequestId() != null ? itemRequestRepository.findById(itemDto.getRequestId()).orElse(null) : null;
        Item item = ItemMapper.toModel(itemDto, owner, request);
        Item savedItem = itemRepository.save(item);
        log.debug("Вещь успешно добавлена: {}", savedItem);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, Long userId, @Valid ItemUpdateDto itemUpdateDto) {
        log.debug("Обновление вещи с id={} пользователем с id={}, данными: {}", itemId, userId, itemUpdateDto);
        if (userId == null) {
            throw new ValidationException("Идентификатор пользователя отсутствует");
        }
        if (itemId == null) {
            throw new ValidationException("Идентификатор вещи отсутствует");
        }
        Item existingItem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Пользователь не является владельцем вещи");
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<ItemDto> getItems(Long userId, int from, int size) {
        log.debug("Получение всех вещей пользователя с id={}", userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        List<Item> items = itemRepository.findByOwnerId(userId, pageable).getContent();
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        List<Comment> comments = commentRepository.findByItemIdIn(itemIds);
        List<Booking> lastBookings = bookingRepository.findLastBookingsByItemIds(itemIds, LocalDateTime.now(), BookingStatus.APPROVED);
        List<Booking> nextBookings = bookingRepository.findNextBookingsByItemIds(itemIds, LocalDateTime.now(), BookingStatus.APPROVED);
        return items.stream()
                .map(item -> {
                    List<Comment> itemComments = comments.stream()
                            .filter(comment -> comment.getItem().getId().equals(item.getId()))
                            .collect(Collectors.toList());
                    Booking lastBooking = lastBookings.stream()
                            .filter(booking -> booking.getItem().getId().equals(item.getId()))
                            .findFirst().orElse(null);
                    Booking nextBooking = nextBookings.stream()
                            .filter(booking -> booking.getItem().getId().equals(item.getId()))
                            .findFirst().orElse(null);
                    return ItemMapper.toItemDto(item, itemComments, lastBooking, nextBooking);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItems(String text, int from, int size) {
        log.debug("Поиск вещей с текстом: {}", text);
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        return itemRepository.search(text, pageable).stream()
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
