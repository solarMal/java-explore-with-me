package ru.practicum.event.entity;

import ru.practicum.exception.ValidationException;

public enum EventState {
    PENDING,
    PUBLISHED,
    CANCELED;

    public static EventState parseState(String state) {
        for (EventState enumState : EventState.values()) {
            if (enumState.name().equalsIgnoreCase(state)) {
                return enumState;
            }
        }
        throw new ValidationException("Unknown state: " + state);
    }
}