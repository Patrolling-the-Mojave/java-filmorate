package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.enums.FriendshipStatus;

@Data
@AllArgsConstructor
public class Friendship {
    private User friend;
    @JsonIgnore
    private FriendshipStatus friendshipStatus;
}
