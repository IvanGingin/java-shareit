package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ItemDto itemDto = new ItemDto(1L, "Item", "Description", true, 1L, null, null, null, null);
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Description", now, 1L, Collections.singletonList(itemDto));
        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Description");
        assertThat(result).hasJsonPathStringValue("$.created");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(now.format(formatter));
        assertThat(result).hasJsonPathNumberValue("$.requestorId");
        assertThat(result).extractingJsonPathNumberValue("$.requestorId").isEqualTo(1);
        assertThat(result).hasJsonPathArrayValue("$.items");
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Item");
    }

    @Test
    void testDeserialize() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String content = String.format(
                "{\"id\":1,\"description\":\"Description\",\"created\":\"%s\",\"requestorId\":1,\"items\":[{\"id\":1,\"name\":\"Item\",\"description\":\"Description\",\"available\":true,\"owner\":1}]}",
                now.format(formatter));
        ObjectContent<ItemRequestDto> result = json.parse(content);
        assertThat(result).isEqualTo(new ItemRequestDto(1L, "Description", now, 1L, Collections.singletonList(new ItemDto(1L, "Item", "Description", true, 1L, null, null, null, null))));
        assertThat(result.getObject().getId()).isEqualTo(1);
        assertThat(result.getObject().getDescription()).isEqualTo("Description");
        assertThat(result.getObject().getCreated()).isEqualTo(now);
        assertThat(result.getObject().getRequestorId()).isEqualTo(1);
        assertThat(result.getObject().getItems()).hasSize(1);
        assertThat(result.getObject().getItems().get(0).getId()).isEqualTo(1);
        assertThat(result.getObject().getItems().get(0).getName()).isEqualTo("Item");
    }

    @Test
    void testDeserializeMissingFields() throws Exception {
        String content = "{\"id\":1,\"description\":\"Description\",\"requestorId\":1}";
        ObjectContent<ItemRequestDto> result = json.parse(content);
        assertThat(result.getObject().getId()).isEqualTo(1);
        assertThat(result.getObject().getDescription()).isEqualTo("Description");
        assertThat(result.getObject().getCreated()).isNull();
        assertThat(result.getObject().getRequestorId()).isEqualTo(1);
        assertThat(result.getObject().getItems()).isNull();
    }

    @Test
    void testDeserializeAdditionalFields() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String content = String.format(
                "{\"id\":1,\"description\":\"Description\",\"created\":\"%s\",\"requestorId\":1,\"items\":[{\"id\":1,\"name\":\"Item\",\"description\":\"Description\",\"available\":true,\"owner\":1}],\"extraField\":\"extraValue\"}",
                now.format(formatter));
        ObjectContent<ItemRequestDto> result = json.parse(content);
        assertThat(result.getObject().getId()).isEqualTo(1);
        assertThat(result.getObject().getDescription()).isEqualTo("Description");
        assertThat(result.getObject().getCreated()).isEqualTo(now);
        assertThat(result.getObject().getRequestorId()).isEqualTo(1);
        assertThat(result.getObject().getItems()).hasSize(1);
        assertThat(result.getObject().getItems().get(0).getId()).isEqualTo(1);
        assertThat(result.getObject().getItems().get(0).getName()).isEqualTo("Item");
    }
}
