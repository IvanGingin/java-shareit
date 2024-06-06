package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
}
