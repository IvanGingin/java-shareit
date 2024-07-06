package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.Constants;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemServiceImpl;

    @PostMapping
    public ItemDto addItem(@RequestHeader(Constants.CONST_SHARED_USER_ID) Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.debug("Получен запрос на добавление вещи от пользователя с id={}, данными: {}", userId, itemDto);
        return itemServiceImpl.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId, @RequestHeader(Constants.CONST_SHARED_USER_ID) Long userId, @Valid @RequestBody ItemUpdateDto itemUpdateDto) {
        log.debug("Получен запрос на обновление вещи с id={} от пользователя с id={}, данными: {}", itemId, userId, itemUpdateDto);
        return itemServiceImpl.updateItem(itemId, userId, itemUpdateDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId, @RequestHeader(Constants.CONST_SHARED_USER_ID) Long userId) {
        log.debug("Получен запрос на получение вещи с id={} пользователем с id={}", itemId, userId);
        return itemServiceImpl.getItem(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(Constants.CONST_SHARED_USER_ID) Long userId,
                                  @RequestParam(defaultValue = "0") int from,
                                  @RequestParam(defaultValue = "10") int size) {
        log.debug("Получен запрос на получение всех вещей пользователя с id={}, с параметрами from={}, size={}", userId, from, size);
        return itemServiceImpl.getItems(userId, from, size);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        log.debug("Получен запрос на удаление вещи с id={}", itemId);
        itemServiceImpl.deleteItem(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "10") int size) {
        log.debug("Получен запрос на поиск вещей с текстом: {}, с параметрами from={}, size={}", text, from, size);
        return itemServiceImpl.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(Constants.CONST_SHARED_USER_ID) Long userId, @PathVariable Long itemId, @Valid @RequestBody CommentDto commentDto) {
        log.debug("Получен запрос на добавление комментария от пользователя с id={} к вещи с id={}, данными: {}", userId, itemId, commentDto);
        return itemServiceImpl.addComment(userId, itemId, commentDto);
    }
}
