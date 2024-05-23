package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class InMemoryItemDao implements ItemDao {
    private final Map<Long, Item> items = new HashMap<>();
    private long itemIdGenerator = 0;

    @Override
    public Item addItem(Long userId, ItemDto itemDto) {
        log.debug("Добавление вещи с данными: {}", itemDto);
        long itemId = ++itemIdGenerator;
        User owner = new User(userId, "ownerName", "owner@example.com"); // Заглушка
        ItemRequest request = null; // Заглушка
        Item item = ItemMapper.toModel(itemDto, owner, request);
        item.setId(itemId);
        items.put(itemId, item);
        log.debug("Вещь успешно добавлена с id={}", itemId);
        return item;
    }

    @Override
    public void updateItem(Long userId, ItemDto itemDto, Long itemId) {
        log.debug("Обновление вещи с id={} от пользователя с id={}, данными: {}", itemId, userId, itemDto);
        Item existingItem = items.get(itemId);
        if (existingItem != null && existingItem.getOwner().getId().equals(userId)) {
            if (itemDto.getName() != null) {
                existingItem.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                existingItem.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                existingItem.setAvailable(itemDto.getAvailable());
            }
            items.put(itemId, existingItem);
            log.debug("Вещь с id={} успешно обновлена", itemId);
        } else {
            log.error("Вещь с id={} не найдена или не принадлежит пользователю с id={}", itemId, userId);
        }
    }

    @Override
    public Item getItem(Long itemId) {
        log.debug("Получение вещи с id={}", itemId);
        Item item = items.get(itemId);
        if (item == null) {
            log.error("Вещь с id={} не найдена!", itemId);
        }
        return item;
    }

    @Override
    public List<Item> getItems(Long userId) {
        log.debug("Получение всех вещей пользователя с id={}", userId);
        List<Item> userItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId().equals(userId)) {
                userItems.add(item);
            }
        }
        return userItems;
    }

    @Override
    public List<Item> searchItems(String text) {
        log.debug("Поиск вещей с текстом: {}", text);
        List<Item> searchResults = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getAvailable() && (item.getName().toLowerCase().contains(text.toLowerCase()) || item.getDescription().toLowerCase().contains(text.toLowerCase()))) {
                searchResults.add(item);
            }
        }
        return searchResults;
    }

    @Override
    public void deleteItem(Long itemId) {
        log.debug("Удаление вещи с id={}", itemId);
        items.remove(itemId);
        log.debug("Вещь с id={} успешно удалена", itemId);
    }
}
