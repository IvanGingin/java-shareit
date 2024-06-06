package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.Constants;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader(Constants.CONST_SHARED_USER_ID) Long userId, @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("Получен запрос на добавление запроса вещи от пользователя с id={}, данными: {}", userId, itemRequestDto);
        return itemRequestService.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader(Constants.CONST_SHARED_USER_ID) Long userId) {
        log.debug("Получен запрос на получение всех запросов пользователя с id={}", userId);
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@PathVariable Long requestId, @RequestHeader(Constants.CONST_SHARED_USER_ID) Long userId) {
        log.debug("Получен запрос на получение запроса вещи с id={} от пользователя с id={}", requestId, userId);
        return itemRequestService.getRequest(requestId, userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(Constants.CONST_SHARED_USER_ID) Long userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size) {
        log.debug("Получен запрос на получение всех запросов вещей с пользователя с id={}, с параметрами from={}, size={}", userId, from, size);
        return itemRequestService.getAllRequests(userId, from, size);
    }
}

