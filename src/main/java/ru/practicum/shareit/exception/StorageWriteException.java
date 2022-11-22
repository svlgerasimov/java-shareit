package ru.practicum.shareit.exception;

public class StorageWriteException extends RuntimeException {
    public StorageWriteException(String message) {
        super(message);
    }
}
