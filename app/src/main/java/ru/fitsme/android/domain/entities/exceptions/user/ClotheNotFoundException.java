package ru.fitsme.android.domain.entities.exceptions.user;

public class ClotheNotFoundException extends UserException {

    public static final int CODE = 100013;

    public ClotheNotFoundException(){}

    public ClotheNotFoundException(String message) {
        super(message);
    }
}
