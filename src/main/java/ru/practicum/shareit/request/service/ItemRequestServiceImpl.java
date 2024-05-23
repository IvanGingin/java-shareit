package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestDao itemRequestDao;
    private final UserDao userDao;

    @Override
    public ItemRequestDto addRequest(Long userId, @Valid ItemRequestDto itemRequestDto) {
        log.debug("Добавление запроса вещи пользователем с id={}, данными: {}", userId, itemRequestDto);
        try {
            User requestor = userDao.getUser(userId);
            if (requestor == null) {
                throw new IllegalArgumentException("Пользователь не найден");
            }

            ItemRequest itemRequest = ItemRequestMapper.toModel(itemRequestDto, requestor);
            ItemRequestDto createdRequest = ItemRequestMapper.toItemRequestDto(itemRequestDao.addRequest(userId, itemRequest));
            log.debug("Запрос вещи успешно добавлен: {}", createdRequest);
            return createdRequest;
        } catch (Exception e) {
            log.error("Ошибка при добавлении запроса вещи: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ItemRequestDto getRequest(Long requestId, Long userId) {
        log.debug("Получение запроса вещи с id={} пользователем с id={}", requestId, userId);
        try {
            ItemRequest itemRequest = itemRequestDao.getRequest(requestId);
            if (itemRequest == null || !itemRequest.getRequest().getId().equals(userId)) {
                throw new IllegalArgumentException("Некорректные данные запроса вещи");
            }

            ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
            log.debug("Запрос вещи с id={} успешно получен: {}", requestId, itemRequestDto);
            return itemRequestDto;
        } catch (Exception e) {
            log.error("Ошибка при получении запроса вещи: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        log.debug("Получение всех запросов пользователя с id={}", userId);
        try {
            List<ItemRequest> requests = itemRequestDao.getUserRequests(userId);
            List<ItemRequestDto> requestDtoList = new ArrayList<>();
            for (ItemRequest request : requests) {
                requestDtoList.add(ItemRequestMapper.toItemRequestDto(request));
            }
            log.debug("Всего запросов найдено: {}", requestDtoList.size());
            return requestDtoList;
        } catch (Exception e) {
            log.error("Ошибка при получении списка запросов пользователя: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        log.debug("Получение всех запросов вещей пользователя с id={} с параметрами from={}, size={}", userId, from, size);
        try {
            List<ItemRequest> requests = itemRequestDao.getAllRequests(from, size);
            List<ItemRequestDto> requestDtoList = new ArrayList<>();
            for (ItemRequest request : requests) {
                requestDtoList.add(ItemRequestMapper.toItemRequestDto(request));
            }
            log.debug("Всего запросов найдено: {}", requestDtoList.size());
            return requestDtoList;
        } catch (Exception e) {
            log.error("Ошибка при получении списка запросов вещей: {}", e.getMessage(), e);
            throw e;
        }
    }
}
