package ru.yandex.practicum.filmorate.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Updates {
    public static void runIfNotNull(Object value, Runnable operation) {
        if (value != null) {
            operation.run();
        }
    }

}
