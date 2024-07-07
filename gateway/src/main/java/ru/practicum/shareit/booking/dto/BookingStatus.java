package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BookingStatus {
    WAITING,
    APPROVED,
    REJECTED,
    PAST,
    CANCELED;
}
