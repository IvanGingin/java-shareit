package ru.practicum.shareit.request.dao;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestDao {
    ItemRequest addRequest(Long userId, ItemRequest itemRequest);

    ItemRequest getRequest(Long requestId);

    List<ItemRequest> getUserRequests(Long userId);

    List<ItemRequest> getAllRequests(int from, int size);
}
