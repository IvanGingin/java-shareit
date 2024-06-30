package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTest {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Test
    void testGetAllRequestsIntegration() {
        User user = new User(null, "User", "user@example.com");
        User requester = new User(null, "Requester", "requester@example.com");
        userRepository.save(user);
        userRepository.save(requester);
        ItemRequest itemRequest1 = new ItemRequest(null, "описание1", requester, LocalDateTime.now().minusDays(1));
        ItemRequest itemRequest2 = new ItemRequest(null, "описание2", requester, LocalDateTime.now().minusDays(2));
        itemRequestRepository.save(itemRequest1);
        itemRequestRepository.save(itemRequest2);
        Item item1 = new Item(null, "Предмет1", "Описание предмета 1", true, user, itemRequest1);
        Item item2 = new Item(null, "Предмет2", "Описание предмета 2", true, user, itemRequest2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        List<ItemRequestDto> itemRequestDtos = itemRequestService.getAllRequests(user.getId(), 0, 10);
        assertThat(itemRequestDtos, notNullValue());
        assertThat(itemRequestDtos, hasSize(2));
        ItemRequestDto requestDto1 = itemRequestDtos.stream()
                .filter(dto -> dto.getDescription().equals("описание1"))
                .findFirst()
                .orElse(null);
        ItemRequestDto requestDto2 = itemRequestDtos.stream()
                .filter(dto -> dto.getDescription().equals("описание2"))
                .findFirst()
                .orElse(null);
        assertThat(requestDto1, notNullValue());
        assertThat(requestDto2, notNullValue());
        assertThat(requestDto1.getItems(), hasSize(1));
        assertThat(requestDto1.getItems().get(0).getName(), equalTo("Предмет1"));
        assertThat(requestDto2.getItems(), hasSize(1));
        assertThat(requestDto2.getItems().get(0).getName(), equalTo("Предмет2"));
    }
}
