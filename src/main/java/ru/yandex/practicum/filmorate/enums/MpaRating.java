package ru.yandex.practicum.filmorate.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MpaRating {
    G("у фильма нет возрастных ограничений"),
    PG("детям рекомендуется смотреть фильм с родителями"),
    PG_13("детям до 13 лет просмотр не желателен"),
    R("лицам до 17 лет просматривать фильм можно только в присутствии взрослого"),
    NC_17("лицам до 18 лет просмотр запрещён");

    private final String description;
}
