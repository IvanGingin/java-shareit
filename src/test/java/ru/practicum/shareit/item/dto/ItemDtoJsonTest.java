package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingGetDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testSerialize() throws Exception {
        BookingGetDto lastBooking = new BookingGetDto(1L, 2L);
        BookingGetDto nextBooking = new BookingGetDto(2L, 3L);
        CommentDto comment = new CommentDto(1L, "Комментарий", 1L, 1L, "Автор", null);
        ItemDto itemDto = new ItemDto(1L, "Вещь", "Описание", true, 1L, 1L, lastBooking, nextBooking, List.of(comment));
        JsonContent<ItemDto> result = json.write(itemDto);
        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).hasJsonPathStringValue("$.name");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Вещь");
        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание");
        assertThat(result).hasJsonPathBooleanValue("$.available");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).hasJsonPathNumberValue("$.owner");
        assertThat(result).extractingJsonPathNumberValue("$.owner").isEqualTo(1);
        assertThat(result).hasJsonPathNumberValue("$.requestId");
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
        assertThat(result).hasJsonPathMapValue("$.lastBooking");
        assertThat(result).hasJsonPathMapValue("$.nextBooking");
        assertThat(result).hasJsonPathArrayValue("$.comments");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"id\":1,\"name\":\"Вещь\",\"description\":\"Описание\",\"available\":true,\"owner\":1,\"requestId\":1,\"lastBooking\":{\"id\":1,\"bookerId\":2},\"nextBooking\":{\"id\":2,\"bookerId\":3},\"comments\":[{\"id\":1,\"text\":\"Комментарий\",\"itemId\":1,\"authorId\":1,\"authorName\":\"Автор\"}]}";
        ObjectContent<ItemDto> result = json.parse(content);
        BookingGetDto lastBooking = new BookingGetDto(1L, 2L);
        BookingGetDto nextBooking = new BookingGetDto(2L, 3L);
        CommentDto comment = new CommentDto(1L, "Комментарий", 1L, 1L, "Автор", null);
        ItemDto expectedItemDto = new ItemDto(1L, "Вещь", "Описание", true, 1L, 1L, lastBooking, nextBooking, List.of(comment));
        assertThat(result).isEqualTo(expectedItemDto);
        assertThat(result.getObject().getId()).isEqualTo(1);
        assertThat(result.getObject().getName()).isEqualTo("Вещь");
        assertThat(result.getObject().getDescription()).isEqualTo("Описание");
        assertThat(result.getObject().getAvailable()).isEqualTo(true);
        assertThat(result.getObject().getOwner()).isEqualTo(1);
        assertThat(result.getObject().getRequestId()).isEqualTo(1);
        assertThat(result.getObject().getLastBooking()).isEqualTo(lastBooking);
        assertThat(result.getObject().getNextBooking()).isEqualTo(nextBooking);
        assertThat(result.getObject().getComments()).containsExactly(comment);
    }
}
