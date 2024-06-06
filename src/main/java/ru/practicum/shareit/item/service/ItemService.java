package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long itemId, Long userId, ItemUpdateDto itemUpdateDto);

    ItemDto getItem(Long itemId);

    List<ItemDto> getItems(Long userId);

    List<ItemDto> searchItems(String text);

    void deleteItem(Long itemId);
}
