package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingGetDtoJsonTest {

    @Autowired
    private JacksonTester<BookingGetDto> json;

    @Test
    void testSerialize() throws Exception {
        BookingGetDto bookingGetDto = new BookingGetDto(1L, 2L);
        JsonContent<BookingGetDto> result = json.write(bookingGetDto);
        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).hasJsonPathNumberValue("$.bookerId");
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"id\":1,\"bookerId\":2}";
        ObjectContent<BookingGetDto> result = json.parse(content);
        assertThat(result).isEqualTo(new BookingGetDto(1L, 2L));
        assertThat(result.getObject().getId()).isEqualTo(1);
        assertThat(result.getObject().getBookerId()).isEqualTo(2);
    }

    @Test
    void testDeserializeMissingFields() throws Exception {
        String content = "{\"id\":1}";
        ObjectContent<BookingGetDto> result = json.parse(content);
        assertThat(result.getObject().getId()).isEqualTo(1);
        assertThat(result.getObject().getBookerId()).isNull();
    }

    @Test
    void testDeserializeAdditionalFields() throws Exception {
        String content = "{\"id\":1,\"bookerId\":2,\"extraField\":\"extraValue\"}";
        ObjectContent<BookingGetDto> result = json.parse(content);
        assertThat(result.getObject().getId()).isEqualTo(1);
        assertThat(result.getObject().getBookerId()).isEqualTo(2);
    }
}
