package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testSerialize() throws Exception {
        UserDto userDto = new UserDto(1L, "Иван Иванов", "ivan.ivanov@example.com");

        JsonContent<UserDto> result = json.write(userDto);

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

        ObjectContent<UserDto> result = json.parse(content);

        assertThat(result).isEqualTo(new UserDto(1L, "Иван Иванов", "ivan.ivanov@example.com"));
        assertThat(result.getObject().getId()).isEqualTo(1);
        assertThat(result.getObject().getName()).isEqualTo("Иван Иванов");
        assertThat(result.getObject().getEmail()).isEqualTo("ivan.ivanov@example.com");
    }
}
