package ru.fitsme.android.data.repositories;

import android.support.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.fitsme.android.domain.boundaries.ITextValidator;
import ru.fitsme.android.domain.entities.exceptions.LoginIncorrectException;
import ru.fitsme.android.domain.entities.exceptions.PasswordIncorrectException;

@Singleton
public class TextValidatorDebug implements ITextValidator {

    @Inject
    public TextValidatorDebug() {
    }

    @Override
    public void checkLogin(@Nullable String login) throws LoginIncorrectException {

        if (login == null || login.length() < 3)
            throw new LoginIncorrectException();
    }

    @Override
    public void checkPassword(@Nullable String password) throws PasswordIncorrectException {
        if (password == null || password.length() < 3)
            throw new PasswordIncorrectException();
    }
}
