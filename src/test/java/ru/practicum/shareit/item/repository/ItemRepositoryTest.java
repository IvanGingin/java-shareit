package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void testFindByOwnerId() {
        User owner = new User();
        owner.setName("Иван Иванов");
        owner.setEmail("ivan.ivanov@example.com");
        entityManager.persist(owner);
        Item item1 = new Item();
        item1.setName("Предмет1");
        item1.setDescription("Описание1");
        item1.setAvailable(true);
        item1.setOwner(owner);
        entityManager.persist(item1);
        Item item2 = new Item();
        item2.setName("Предмет2");
        item2.setDescription("Описание2");
        item2.setAvailable(true);
        item2.setOwner(owner);
        entityManager.persist(item2);
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> items = itemRepository.findByOwnerId(owner.getId(), pageable).getContent();
        assertThat(items).hasSize(2).extracting(Item::getName).contains("Предмет1", "Предмет2");
    }

    @Test
    public void testSearch() {
        User owner = new User();
        owner.setName("Мария Петрова");
        owner.setEmail("maria.petrova@example.com");
        entityManager.persist(owner);
        Item item = new Item();
        item.setName("Специальный предмет");
        item.setDescription("Это специальный предмет.");
        item.setAvailable(true);
        item.setOwner(owner);
        entityManager.persist(item);
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> items = itemRepository.search("специальный", pageable).getContent();
        assertThat(items).hasSize(1).extracting(Item::getName).contains("Специальный предмет");
    }
}
