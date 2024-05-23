package ru.practicum.shareit.request.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class InMemoryItemRequestDao implements ItemRequestDao {
    private final Map<Long, ItemRequest> requests = new HashMap<>();
    private long requestIdGenerator = 0;

    @Override
    public ItemRequest addRequest(Long userId, ItemRequest itemRequest) {
        log.debug("Добавление запроса вещи с данными: {}", itemRequest);
        long requestId = ++requestIdGenerator;
        itemRequest.setId(requestId);
        requests.put(requestId, itemRequest);
        log.debug("Запрос вещи успешно добавлен с id={}", requestId);
        return itemRequest;
    }

    @Override
    public ItemRequest getRequest(Long requestId) {
        log.debug("Получение запроса вещи с id={}", requestId);
        ItemRequest request = requests.get(requestId);
        if (request == null) {
            log.error("Запрос вещи с id={} не найден!", requestId);
        }
        return request;
    }

    @Override
    public List<ItemRequest> getUserRequests(Long userId) {
        log.debug("Получение всех запросов пользователя с id={}", userId);
        List<ItemRequest> result = new ArrayList<>();
        for (ItemRequest request : requests.values()) {
            if (request.getRequest().getId().equals(userId)) {
                result.add(request);
            }
        }
        return result;
    }

    @Override
    public List<ItemRequest> getAllRequests(int from, int size) {
        log.debug("Получение всех запросов вещей с параметрами from={}, size={}", from, size);
        List<ItemRequest> allRequests = new ArrayList<>(requests.values());
        int toIndex = Math.min(from + size, allRequests.size());
        return allRequests.subList(from, toIndex);
    }
}

