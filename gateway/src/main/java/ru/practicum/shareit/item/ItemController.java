package ru.practicum.shareit.item;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import javax.validation.Valid;

import static ru.practicum.shareit.util.Constants.CONST_SHARED_USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(CONST_SHARED_USER_ID) long userId,
                                          @Valid @RequestBody ItemDto itemDto) {
        log.debug("Получен запрос на добавление вещи от пользователя с id={}, данными: {}", userId, itemDto);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId,
                                             @RequestHeader(CONST_SHARED_USER_ID) long userId,
                                             @Valid @RequestBody ItemUpdateDto itemUpdateDto) {
        log.debug("Получен запрос на обновление вещи с id={} от пользователя с id={}, данными: {}", itemId, userId, itemUpdateDto);
        return itemClient.updateItem(userId, itemId, itemUpdateDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable Long itemId,
                                          @RequestHeader(CONST_SHARED_USER_ID) long userId) {
        log.debug("Получен запрос на получение вещи с id={} пользователем с id={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(CONST_SHARED_USER_ID) long userId,
                                           @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        log.debug("Получен запрос на получение всех вещей пользователя с id={}, с параметрами from={}, size={}", userId, from, size);
        return itemClient.getItems(userId, from, size);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@PathVariable Long itemId) {
        log.debug("Получен запрос на удаление вещи с id={}", itemId);
        return itemClient.deleteItem(itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {
        log.debug("Получен запрос на поиск вещей с текстом: {}, с параметрами from={}, size={}", text, from, size);
        return itemClient.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(CONST_SHARED_USER_ID) long userId,
                                             @PathVariable Long itemId,
                                             @Valid @RequestBody CommentDto commentDto) {
        log.debug("Получен запрос на добавление комментария от пользователя с id={} к вещи с id={}, данными: {}", userId, itemId, commentDto);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
