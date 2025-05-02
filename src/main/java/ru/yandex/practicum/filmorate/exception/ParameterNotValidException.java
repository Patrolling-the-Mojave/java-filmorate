package ru.yandex.practicum.filmorate.exception;


public class ParameterNotValidException extends IllegalArgumentException {
    public ParameterNotValidException(String message, int parameter) {
        super(message);
    }
}
