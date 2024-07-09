package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    public void testFindByItemId() {
        User user = new User();
        user.setName("Пользователь");
        user.setEmail("user@example.com");
        entityManager.persist(user);
        Item item = new Item();
        item.setName("Предмет");
        item.setDescription("Описание предмета");
        item.setAvailable(true);
        item.setOwner(user);
        entityManager.persist(item);
        Comment comment1 = new Comment();
        comment1.setText("Комментарий1");
        comment1.setItem(item);
        comment1.setAuthor(user);
        entityManager.persist(comment1);
        Comment comment2 = new Comment();
        comment2.setText("Комментарий2");
        comment2.setItem(item);
        comment2.setAuthor(user);
        entityManager.persist(comment2);
        List<Comment> comments = commentRepository.findByItemId(item.getId());
        assertThat(comments).hasSize(2).extracting(Comment::getText).contains("Комментарий1", "Комментарий2");
    }
}
