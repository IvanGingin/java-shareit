package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    public void testFindAllByRequestorId() {
        User requestor = new User();
        requestor.setName("Иван Иванов");
        requestor.setEmail("ivan.ivanov@example.com");
        entityManager.persist(requestor);

        ItemRequest request1 = new ItemRequest();
        request1.setRequestor(requestor);
        request1.setDescription("Request 1 Description");
        entityManager.persist(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setRequestor(requestor);
        request2.setDescription("Request 2 Description");
        entityManager.persist(request2);

        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorId(requestor.getId());
        assertThat(requests).hasSize(2).extracting(ItemRequest::getDescription).contains("Request 1 Description", "Request 2 Description");
    }

    @Test
    public void testFindAmountOfRequests() {
        User requestor = new User();
        requestor.setName("Мария Петрова");
        requestor.setEmail("maria.petrova@example.com");
        entityManager.persist(requestor);

        ItemRequest request1 = new ItemRequest();
        request1.setRequestor(requestor);
        request1.setDescription("Request 1 Description");
        entityManager.persist(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setRequestor(requestor);
        request2.setDescription("Request 2 Description");
        entityManager.persist(request2);

        int amount = itemRequestRepository.findAmountOfRequests(requestor.getId());
        assertThat(amount).isEqualTo(2);
    }

    @Test
    public void testFindAllInPage() {
        User requestor1 = new User();
        requestor1.setName("Иван Иванов");
        requestor1.setEmail("ivan.ivanov@example.com");
        entityManager.persist(requestor1);

        User requestor2 = new User();
        requestor2.setName("Мария Петрова");
        requestor2.setEmail("maria.petrova@example.com");
        entityManager.persist(requestor2);

        ItemRequest request1 = new ItemRequest();
        request1.setRequestor(requestor1);
        request1.setDescription("Request 1 Description");
        entityManager.persist(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setRequestor(requestor2);
        request2.setDescription("Request 2 Description");
        entityManager.persist(request2);

        Pageable pageable = PageRequest.of(0, 10);
        List<ItemRequest> requests = itemRequestRepository.findAllInPage(requestor1.getId(), pageable);
        assertThat(requests).hasSize(1).extracting(ItemRequest::getDescription).contains("Request 2 Description");
    }
}
