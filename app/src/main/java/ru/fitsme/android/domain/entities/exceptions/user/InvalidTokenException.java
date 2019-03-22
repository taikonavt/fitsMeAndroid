package ru.fitsme.android.domain.entities.exceptions.user;

public class InvalidTokenException extends UserException {

    public static final int CODE = 100006;

    public InvalidTokenException(){}

    public InvalidTokenException(String message) {
        super(message);
    }
}
