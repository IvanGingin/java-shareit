package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {
    Item addItem(Long userId, ItemDto itemDto);

    void updateItem(Long userId, ItemDto itemDto, Long itemId);

    Item getItem(Long itemId);

    List<Item> getItems(Long userId);

    List<Item> searchItems(String text);

    void deleteItem(Long itemId);
}


