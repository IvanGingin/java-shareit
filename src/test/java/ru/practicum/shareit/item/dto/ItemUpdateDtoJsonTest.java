package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemUpdateDtoJsonTest {

    @Autowired
    private JacksonTester<ItemUpdateDto> json;

    @Test
    void testSerialize() throws Exception {
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(1L, "Обновленная вещь", "Обновленное описание", true);
        JsonContent<ItemUpdateDto> result = json.write(itemUpdateDto);
        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).hasJsonPathStringValue("$.name");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Обновленная вещь");
        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Обновленное описание");
        assertThat(result).hasJsonPathBooleanValue("$.available");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"id\":1,\"name\":\"Обновленная вещь\",\"description\":\"Обновленное описание\",\"available\":true}";
        ObjectContent<ItemUpdateDto> result = json.parse(content);
        assertThat(result).isEqualTo(new ItemUpdateDto(1L, "Обновленная вещь", "Обновленное описание", true));
        assertThat(result.getObject().getId()).isEqualTo(1);
        assertThat(result.getObject().getName()).isEqualTo("Обновленная вещь");
        assertThat(result.getObject().getDescription()).isEqualTo("Обновленное описание");
        assertThat(result.getObject().getAvailable()).isEqualTo(true);
    }
}
