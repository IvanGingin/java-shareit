package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingGetDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Название вещи отсутствует")
    private String name;
    @NotBlank(message = "Описание вещи отсутствует")
    private String description;
    @NotNull(message = "Статус вещи отсутствует")
    private Boolean available;
    private Long owner;
    private Long request;
    private BookingGetDto lastBooking;
    private BookingGetDto nextBooking;
    private List<CommentDto> comments;
}
