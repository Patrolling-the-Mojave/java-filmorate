package ru.yandex.practicum.filmorate.storage;

import java.util.Set;


public interface LikeStorage {
    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    Set<Integer> getLikes(int filmId);
}
