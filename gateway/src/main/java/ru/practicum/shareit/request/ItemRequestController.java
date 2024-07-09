package ru.practicum.shareit.request;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constants.CONST_SHARED_USER_ID;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(CONST_SHARED_USER_ID) long userId,
                                             @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("Получен запрос на добавление запроса вещи от пользователя с id={}, данными: {}", userId, itemRequestDto);
        return itemRequestClient.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader(CONST_SHARED_USER_ID) long userId) {
        log.debug("Получен запрос на получение всех запросов пользователя с id={}", userId);
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable Long requestId,
                                             @RequestHeader(CONST_SHARED_USER_ID) long userId) {
        log.debug("Получен запрос на получение запроса вещи с id={} от пользователя с id={}", requestId, userId);
        return itemRequestClient.getRequest(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(CONST_SHARED_USER_ID) long userId,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                 @RequestParam(defaultValue = "20") @Min(1) int size) {
        log.debug("Получен запрос на получение всех запросов вещей пользователя с id={}, с параметрами from={}, size={}", userId, from, size);
        return itemRequestClient.getAllRequests(userId, from, size);
    }
}
