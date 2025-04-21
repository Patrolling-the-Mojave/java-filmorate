package ru.yandex.practicum.filmorate.util;

public class Updates {
    public static void runIfNotNull(Object value, Runnable operation) {
        if (value != null) {
            operation.run();
        }
    }
}
