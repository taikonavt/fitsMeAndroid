package ru.fitsme.android.presentation.fragments.signinup.viewmodel;

import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import ru.fitsme.android.app.App;
import ru.fitsme.android.app.Navigation;
import ru.fitsme.android.domain.interactors.auth.ISignInUpInteractor;
import ru.fitsme.android.presentation.common.livedata.NonNullLiveData;
import ru.fitsme.android.presentation.common.livedata.NonNullMutableLiveData;
import ru.fitsme.android.presentation.fragments.signinup.entities.SignInUpState;

public class SignInViewModel extends ViewModel {

    @Inject
    ISignInUpInteractor signInUpInteractor;

    @Inject
    Navigation navigation;

    private Disposable disposable;
    private NonNullMutableLiveData<SignInUpState> fieldsStateLiveData = new NonNullMutableLiveData<>();

    public SignInViewModel() {
        App.getInstance().getDi().inject(this);
    }

    public void onSignIn(String login, String password) {
        disposable = signInUpInteractor.authorize(login, password)
                .subscribe(signInUpResult -> {
                    SignInUpState signInUpState = new SignInUpState(signInUpResult, false);
                    fieldsStateLiveData.setValue(signInUpState);
                });
    }

    public NonNullLiveData<SignInUpState> getFieldsStateLiveData() {
        return fieldsStateLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        if (disposable != null) {
            disposable.dispose();
        }
    }
}
