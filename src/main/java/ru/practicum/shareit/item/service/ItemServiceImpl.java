package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.exception.NotFoundException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemDao inMemoryItemDao;
    private final UserDao userDao;

    @Override
    public ItemDto addItem(Long userId, @Valid ItemDto itemDto) {
        log.debug("Добавление вещи пользователем с id={}, данными: {}", userId, itemDto);
        try {
            User owner = userDao.getUser(userId);
            if (owner == null) {
                log.error("Пользователь с id={} не найден", userId);
                throw new NotFoundException("Пользователь с id=" + userId + " не найден");
            }

            Item item = ItemMapper.toModel(itemDto, owner, null);
            ItemDto createdItem = ItemMapper.toItemDto(inMemoryItemDao.addItem(userId, itemDto));
            log.debug("Вещь успешно добавлена: {}", createdItem);
            return createdItem;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при добавлении вещи: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, @Valid ItemUpdateDto itemUpdateDto) {
        log.debug("Обновление вещи с id={} пользователем с id={}, данными: {}", itemId, userId, itemUpdateDto);
        try {
            Item existingItem = inMemoryItemDao.getItem(itemId);
            if (existingItem == null) {
                log.error("Вещь с id={} не найдена", itemId);
                throw new NotFoundException("Вещь с id=" + itemId + " не найдена");
            }
            if (!existingItem.getOwner().getId().equals(userId)) {
                log.error("Пользователь с id={} не является владельцем вещи с id={}", userId, itemId);
                throw new ForbiddenException("Пользователь с id=" + userId + " не является владельцем вещи с id=" + itemId);
            }

            if (itemUpdateDto.getName() != null) {
                existingItem.setName(itemUpdateDto.getName());
            }
            if (itemUpdateDto.getDescription() != null) {
                existingItem.setDescription(itemUpdateDto.getDescription());
            }
            if (itemUpdateDto.getAvailable() != null) {
                existingItem.setAvailable(itemUpdateDto.getAvailable());
            }
            inMemoryItemDao.updateItem(userId, itemUpdateDto, itemId);
            log.debug("Вещь с id={} успешно обновлена: {}", itemId, existingItem);
            return ItemMapper.toItemDto(existingItem);
        } catch (NotFoundException | ForbiddenException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при обновлении вещи с id={} пользователем с id={}: {}", itemId, userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ItemDto getItem(Long itemId) {
        log.debug("Получение вещи с id={}", itemId);
        try {
            ItemDto itemDto = ItemMapper.toItemDto(inMemoryItemDao.getItem(itemId));
            if (itemDto == null) {
                log.error("Ошибка при получении вещи с id={}: вещь не найдена", itemId);
            }
            log.debug("Вещь с id={} успешно получена: {}", itemId, itemDto);
            return itemDto;
        } catch (Exception e) {
            log.error("Ошибка при получении вещи с id={}: {}", itemId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        log.debug("Получение всех вещей пользователя с id={}", userId);
        try {
            List<Item> items = inMemoryItemDao.getItems(userId);
            List<ItemDto> itemDtoList = new ArrayList<>();
            for (Item item : items) {
                itemDtoList.add(ItemMapper.toItemDto(item));
            }
            log.debug("Всего вещей найдено: {}", itemDtoList.size());
            return itemDtoList;
        } catch (Exception e) {
            log.error("Ошибка при получении списка вещей пользователя с id={}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        log.debug("Поиск вещей с текстом: {}", text);
        try {
            List<Item> items = inMemoryItemDao.searchItems(text);
            List<ItemDto> itemDtoList = new ArrayList<>();
            for (Item item : items) {
                itemDtoList.add(ItemMapper.toItemDto(item));
            }
            log.debug("Поиск завершен. Найдено вещей: {}", itemDtoList.size());
            return itemDtoList;
        } catch (Exception e) {
            log.error("Ошибка при поиске вещей с текстом '{}': {}", text, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteItem(Long itemId) {
        log.debug("Удаление вещи с id={}", itemId);
        try {
            inMemoryItemDao.deleteItem(itemId);
            log.debug("Вещь с id={} успешно удалена", itemId);
        } catch (Exception e) {
            log.error("Ошибка при удалении вещи с id={}: {}", itemId, e.getMessage(), e);
            throw e;
        }
    }
}
