package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {

    private final EntityManager em;
    private final UserService userService;

    @Test
    void testUpdateUserIntegration() {
        UserDto initialUserDto = new UserDto(null, "Иван Иванов", "ivan.ivanov@example.com");
        UserDto savedUserDto = userService.addUser(initialUserDto);

        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName("Саша Александров");
        userUpdateDto.setEmail("sasha.aleksandrov@example.com");
        userService.updateUser(savedUserDto.getId(), userUpdateDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User updatedUser = query.setParameter("id", savedUserDto.getId()).getSingleResult();

        assertThat(updatedUser.getId(), notNullValue());
        assertThat(updatedUser.getName(), equalTo(userUpdateDto.getName()));
        assertThat(updatedUser.getEmail(), equalTo(userUpdateDto.getEmail()));
    }
}
