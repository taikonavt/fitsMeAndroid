package ru.fitsme.android.presentation.fragments.signinup.entities;

import ru.fitsme.android.domain.entities.signinup.SignInUpResult;

public class SignInUpState {
    private SignInUpResult signInUpResult;
    private boolean loading;

    public SignInUpState(SignInUpResult signInUpResult, boolean loading) {
        this.signInUpResult = signInUpResult;
        this.loading = loading;
    }

    public SignInUpResult getSignInUpResult() {
        return signInUpResult;
    }

    public boolean isLoading() {
        return loading;
    }
}
