package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public ItemRequestDto addRequest(Long userId, ItemRequestDto itemRequestDto) {
        log.debug("Добавление запроса вещи пользователем с id={}, данными: {}", userId, itemRequestDto);
        if (itemRequestDto == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest itemRequest = itemRequestMapper.toModel(itemRequestDto, user);
        itemRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestDto(itemRequest, new ArrayList<>());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getRequest(Long id, Long userId) {
        if (!userRepository.existsById(userId)) {
            log.debug("Объект типа User с id={} отсутствует в базе данных", userId);
            throw new NotFoundException("Пользователь не существует!");
        }
        ItemRequest itemRequest = itemRequestRepository.findById(id).orElseThrow(() -> new NotFoundException("Запрос не найден!"));
        log.debug("Возвращаем объект запроса вещи с id={}", id);
        List<Item> items = itemRepository.findByRequestId(id);
        List<ItemDto> itemDtos = items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return itemRequestMapper.toItemRequestDto(itemRequest, itemDtos);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        log.debug("Получение всех запросов пользователя с id={}", userId);
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(userId);
        return itemRequests.stream()
                .map(itemRequest -> {
                    List<ItemDto> items = itemRepository.findByRequestId(itemRequest.getId()).stream()
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList());
                    return itemRequestMapper.toItemRequestDto(itemRequest, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            log.debug("Объект типа User с id={} отсутствует в базе данных!", userId);
            throw new NotFoundException("Пользователь не найден!");
        }
        int amountOfRequests = itemRequestRepository.findAmountOfRequests(userId);
        int pageNum = amountOfRequests > from ? from / size : 0;
        log.debug("Параметры пагинации: pageNum={}, size={}", pageNum, size);
        Pageable page = PageRequest.of(pageNum, size, Sort.by("created").descending());
        List<ItemRequest> itemRequests = itemRequestRepository.findAllInPage(userId, page);
        List<ItemRequestDto> result = itemRequests.stream()
                .map(itemRequest -> {
                    List<ItemDto> items = itemRepository.findByRequestId(itemRequest.getId()).stream()
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList());
                    return itemRequestMapper.toItemRequestDto(itemRequest, items);
                })
                .collect(Collectors.toList());
        log.debug("Количество возвращаемых запросов: {}", result.size());
        return result;
    }
}

