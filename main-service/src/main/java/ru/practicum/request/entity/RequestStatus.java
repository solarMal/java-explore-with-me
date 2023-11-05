package ru.practicum.request.entity;

import ru.practicum.exception.ValidationException;

public enum RequestStatus {
    CONFIRMED,
    PENDING,
    REJECTED,
    CANCELED;

    public static RequestStatus parseRequestStatus(String status) {
        for (RequestStatus requestStatus : RequestStatus.values()) {
            if (requestStatus.name().equalsIgnoreCase(status)) {
                return requestStatus;
            }
        }
        throw new ValidationException("Unknown state: " + status);
    }
}