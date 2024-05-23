package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto);

    ItemDto getItem(Long itemId);

    List<ItemDto> getItems(Long userId);

    List<ItemDto> searchItems(String text);

    void deleteItem(Long itemId);
}
