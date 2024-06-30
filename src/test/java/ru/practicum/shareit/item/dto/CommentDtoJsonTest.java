package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        CommentDto commentDto = new CommentDto(1L, "Комментарий", 1L, 1L, "Автор", now);
        JsonContent<CommentDto> result = json.write(commentDto);
        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).hasJsonPathStringValue("$.text");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Комментарий");
        assertThat(result).hasJsonPathNumberValue("$.itemId");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).hasJsonPathNumberValue("$.authorId");
        assertThat(result).extractingJsonPathNumberValue("$.authorId").isEqualTo(1);
        assertThat(result).hasJsonPathStringValue("$.authorName");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Автор");
        assertThat(result).hasJsonPathStringValue("$.created");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    void testDeserialize() throws Exception {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        String content = String.format(
                "{\"id\":1,\"text\":\"Комментарий\",\"itemId\":1,\"authorId\":1,\"authorName\":\"Автор\",\"created\":\"%s\"}",
                now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        ObjectContent<CommentDto> result = json.parse(content);
        assertThat(result).isEqualTo(new CommentDto(1L, "Комментарий", 1L, 1L, "Автор", now));
        assertThat(result.getObject().getId()).isEqualTo(1);
        assertThat(result.getObject().getText()).isEqualTo("Комментарий");
        assertThat(result.getObject().getItemId()).isEqualTo(1);
        assertThat(result.getObject().getAuthorId()).isEqualTo(1);
        assertThat(result.getObject().getAuthorName()).isEqualTo("Автор");
        assertThat(result.getObject().getCreated()).isEqualTo(now);
    }

    @Test
    void testDeserializeInvalidText() throws Exception {
        String content = "{\"id\":1,\"text\":\"\",\"itemId\":1,\"authorId\":1,\"authorName\":\"Автор\",\"created\":\"2023-06-25T10:15:30\"}";
        ObjectContent<CommentDto> result = json.parse(content);
        assertThat(result.getObject().getId()).isEqualTo(1);
        assertThat(result.getObject().getText()).isEmpty();
        assertThat(result.getObject().getItemId()).isEqualTo(1);
        assertThat(result.getObject().getAuthorId()).isEqualTo(1);
        assertThat(result.getObject().getAuthorName()).isEqualTo("Автор");
        assertThat(result.getObject().getCreated()).isEqualTo(LocalDateTime.of(2023, 6, 25, 10, 15, 30));
    }
}
