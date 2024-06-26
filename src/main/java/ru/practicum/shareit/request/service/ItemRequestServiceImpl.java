package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto addRequest(Long userId, @Valid ItemRequestDto itemRequestDto) {
        log.debug("Добавление запроса вещи пользователем с id={}, данными: {}", userId, itemRequestDto);
        User requestor = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest itemRequest = ItemRequestMapper.toModel(itemRequestDto, requestor);
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        log.debug("Запрос вещи успешно добавлен: {}", savedRequest);
        return ItemRequestMapper.toItemRequestDto(savedRequest);
    }

    @Override
    public ItemRequestDto getRequest(Long requestId, Long userId) {
        log.debug("Получение запроса вещи с id={} пользователем с id={}", requestId, userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос не найден"));
        if (!itemRequest.getRequestor().getId().equals(userId)) {
            throw new NotFoundException("Запрос не принадлежит пользователю");
        }
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        log.debug("Получение всех запросов пользователя с id={}", userId);
        return itemRequestRepository.findAllByRequestorId(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        log.debug("Получение всех запросов вещей пользователя с id={} с параметрами from={}, size={}", userId, from, size);
        return itemRequestRepository.findAll().stream()
                .skip(from)
                .limit(size)
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }
}
