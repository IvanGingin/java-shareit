package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserUpdateDtoJsonTest {

    @Autowired
    private JacksonTester<UserUpdateDto> json;

    @Test
    void testSerialize() throws Exception {
        UserUpdateDto userUpdateDto = new UserUpdateDto(1L, "Иван Иванов", "ivan.ivanov@example.com");

        JsonContent<UserUpdateDto> result = json.write(userUpdateDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).hasJsonPathStringValue("$.name");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Иван Иванов");
        assertThat(result).hasJsonPathStringValue("$.email");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("ivan.ivanov@example.com");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"id\":1,\"name\":\"Иван Иванов\",\"email\":\"ivan.ivanov@example.com\"}";

        ObjectContent<UserUpdateDto> result = json.parse(content);

        assertThat(result).isEqualTo(new UserUpdateDto(1L, "Иван Иванов", "ivan.ivanov@example.com"));
        assertThat(result.getObject().getId()).isEqualTo(1);
        assertThat(result.getObject().getName()).isEqualTo("Иван Иванов");
        assertThat(result.getObject().getEmail()).isEqualTo("ivan.ivanov@example.com");
    }

    @Test
    void testDeserializeInvalidEmail() throws Exception {
        String content = "{\"id\":1,\"name\":\"Иван Иванов\",\"email\":\"invalid-email\"}";

        ObjectContent<UserUpdateDto> result = json.parse(content);

        assertThat(result.getObject().getId()).isEqualTo(1);
        assertThat(result.getObject().getName()).isEqualTo("Иван Иванов");
        assertThat(result.getObject().getEmail()).isEqualTo("invalid-email");
    }
}
