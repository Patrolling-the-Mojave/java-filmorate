package ru.yandex.practicum.filmorate.storage;

public interface FriendStorage {
    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

}
