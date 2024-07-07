package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class UnsupportedException extends RuntimeException {
    private final String state;

    public UnsupportedException(String message, String state) {
        super(message);
        this.state = state;
    }
}