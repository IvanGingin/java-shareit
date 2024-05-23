package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    @NotNull(message = "Идентификатор вещи отсутствует")
    private Long itemId;
    @NotNull(message = "Дата начала бронирования отсутствует")
    @Future(message = "Дата начала бронирования должна быть в будущем")
    private LocalDateTime start;
    @NotNull(message = "Дата окончания бронирования отсутствует")
    @Future(message = "Дата окончания бронирования должна быть в будущем")
    private LocalDateTime end;
    private Long bookerId;
    private BookingStatus status;
}
