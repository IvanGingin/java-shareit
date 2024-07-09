package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setName("Иван Иванов");
        user.setEmail("ivan.ivanov@example.com");

        User savedUser = userRepository.save(user);
        assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    public void testFindUserById() {
        User user = new User();
        user.setName("Мария Петрова");
        user.setEmail("maria.petrova@example.com");
        entityManager.persist(user);
        entityManager.flush();

        Optional<User> foundUser = userRepository.findById(user.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Мария Петрова");
        assertThat(foundUser.get().getEmail()).isEqualTo("maria.petrova@example.com");
    }

    @Test
    public void testDeleteUser() {
        User user = new User();
        user.setName("Алексей Смирнов");
        user.setEmail("alexey.smirnov@example.com");
        entityManager.persist(user);
        entityManager.flush();

        userRepository.deleteById(user.getId());
        Optional<User> deletedUser = userRepository.findById(user.getId());
        assertThat(deletedUser).isNotPresent();
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setName("Ольга Сидорова");
        user.setEmail("olga.sidorova@example.com");
        entityManager.persist(user);
        entityManager.flush();

        User foundUser = entityManager.find(User.class, user.getId());
        foundUser.setName("Ольга Иванова");
        userRepository.save(foundUser);

        User updatedUser = entityManager.find(User.class, user.getId());
        assertThat(updatedUser.getName()).isEqualTo("Ольга Иванова");
    }
}
