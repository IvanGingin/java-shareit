package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSerialize() throws Exception {
        LocalDateTime created = LocalDateTime.of(2024, 6, 30, 14, 0);
        ItemRequestDto dto = new ItemRequestDto(1L, "Test Description", created, 1L, Collections.emptyList());

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Test Description");
        assertThat(result).hasJsonPathStringValue("$.created");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2024-06-30T14:00:00");
        assertThat(result).hasJsonPathNumberValue("$.requestorId");
        assertThat(result).extractingJsonPathNumberValue("$.requestorId").isEqualTo(1);
    }

    @Test
    public void testDeserialize() throws Exception {
        String content = "{\"id\":1,\"description\":\"Test Description\",\"created\":\"2024-06-30T14:00:00\",\"requestorId\":1,\"items\":[]}";

        ObjectContent<ItemRequestDto> result = json.parse(content);

        assertThat(result).isEqualTo(new ItemRequestDto(1L, "Test Description", LocalDateTime.of(2024, 6, 30, 14, 0), 1L, Collections.emptyList()));
    }

    @Test
    public void testItemRequestMapperWhenCreatedIsNull() {
        ItemRequestDto dto = new ItemRequestDto(null, "Test Description", null, 1L, Collections.emptyList());
        User user = new User();
        user.setId(1L);

        ItemRequestMapper mapper = new ItemRequestMapper();
        LocalDateTime before = LocalDateTime.now();
        ItemRequest itemRequest = mapper.toModel(dto, user);
        LocalDateTime after = LocalDateTime.now();

        assertThat(itemRequest.getDescription()).isEqualTo("Test Description");
        assertThat(itemRequest.getRequestor().getId()).isEqualTo(1L);
        assertThat(itemRequest.getCreated()).isAfterOrEqualTo(before);
        assertThat(itemRequest.getCreated()).isBeforeOrEqualTo(after);
    }
}
