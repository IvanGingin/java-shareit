package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Test
    void testAddCommentIntegration() {
        User user = new User(null, "Иван Иванов", "ivan.ivanov@example.com");
        user = userRepository.save(user);
        ItemDto itemDto = new ItemDto(null, "Предмет 1", "Описание предмета 1", true, null, null, null, null, null);
        Item item = itemRepository.save(ItemMapper.toModel(itemDto, user, null));
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        bookingRepository.save(booking);
        CommentDto commentDto = new CommentDto(null, "Отличный предмет!", null, null, null, null);
        itemService.addComment(user.getId(), item.getId(), commentDto);
        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.item.id = :itemId and c.author.id = :authorId", Comment.class);
        Comment savedComment = query.setParameter("itemId", item.getId())
                .setParameter("authorId", user.getId())
                .getSingleResult();
        assertThat(savedComment.getId(), notNullValue());
        assertThat(savedComment.getText(), equalTo(commentDto.getText()));
        assertThat(savedComment.getItem().getId(), equalTo(item.getId()));
        assertThat(savedComment.getAuthor().getId(), equalTo(user.getId()));
    }

    @Test
    void testGetItemsIntegration() {
        User user = new User(null, "Саша Александров", "sasha.alexandrov@example.com");
        user = userRepository.save(user);
        ItemDto itemDto1 = new ItemDto(null, "Предмет 2", "Описание предмета 2", true, null, null, null, null, null);
        ItemDto itemDto2 = new ItemDto(null, "Предмет 3", "Описание предмета 3", true, null, null, null, null, null);
        itemService.addItem(user.getId(), itemDto1);
        itemService.addItem(user.getId(), itemDto2);
        List<ItemDto> items = itemService.getItems(user.getId(), 0, 10);
        assertThat(items, hasSize(2));
        assertThat(items.get(0).getName(), oneOf(itemDto1.getName(), itemDto2.getName()));
        assertThat(items.get(1).getName(), oneOf(itemDto1.getName(), itemDto2.getName()));
    }
}
