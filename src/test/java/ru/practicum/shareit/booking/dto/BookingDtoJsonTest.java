package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        BookingDto bookingDto = new BookingDto(1L, now.plusDays(1), now.plusDays(2), 1L, null, null, BookingStatus.WAITING);
        JsonContent<BookingDto> result = json.write(bookingDto);
        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).hasJsonPathStringValue("$.start");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(now.plusDays(1).toString());
        assertThat(result).hasJsonPathStringValue("$.end");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(now.plusDays(2).toString());
        assertThat(result).hasJsonPathNumberValue("$.itemId");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).hasJsonPathStringValue("$.status");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(BookingStatus.WAITING.toString());
    }

    @Test
    void testDeserialize() throws Exception {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        String content = String.format(
                "{\"id\":1,\"start\":\"%s\",\"end\":\"%s\",\"itemId\":1,\"status\":\"WAITING\"}",
                now.plusDays(1).toString(), now.plusDays(2).toString());
        ObjectContent<BookingDto> result = json.parse(content);
        assertThat(result).isEqualTo(new BookingDto(1L, now.plusDays(1), now.plusDays(2), 1L, null, null, BookingStatus.WAITING));
        assertThat(result.getObject().getId()).isEqualTo(1);
        assertThat(result.getObject().getStart()).isEqualTo(now.plusDays(1));
        assertThat(result.getObject().getEnd()).isEqualTo(now.plusDays(2));
        assertThat(result.getObject().getItemId()).isEqualTo(1);
        assertThat(result.getObject().getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void testDeserializeInvalidDates() throws Exception {
        String content = "{\"id\":1,\"start\":\"2023-06-25T10:15:30\",\"end\":\"2023-06-24T10:15:30\",\"itemId\":1,\"status\":\"WAITING\"}";
        ObjectContent<BookingDto> result = json.parse(content);
        assertThat(result.getObject().getId()).isEqualTo(1);
        assertThat(result.getObject().getStart()).isEqualTo(LocalDateTime.of(2023, 6, 25, 10, 15, 30));
        assertThat(result.getObject().getEnd()).isEqualTo(LocalDateTime.of(2023, 6, 24, 10, 15, 30));
        assertThat(result.getObject().getItemId()).isEqualTo(1);
        assertThat(result.getObject().getStatus()).isEqualTo(BookingStatus.WAITING);
    }
}
