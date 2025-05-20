package ru.yandex.practicum.filmorate.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FriendshipStatus {
    PENDING("pending"), CONFIRMED("confirmed");

    private final String dbValue;

    public static FriendshipStatus mapFromDbValue(String dbValue) {
        for (FriendshipStatus status : values()) {
            if (status.dbValue.equalsIgnoreCase(dbValue)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Состояние дружбы " + dbValue + " не найдено");
    }
}
