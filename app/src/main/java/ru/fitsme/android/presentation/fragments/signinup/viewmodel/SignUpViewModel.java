package ru.fitsme.android.presentation.fragments.signinup.viewmodel;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import ru.fitsme.android.domain.entities.auth.SignInUpResult;
import ru.fitsme.android.domain.interactors.auth.ISignInteractor;
import ru.fitsme.android.presentation.common.livedata.NonNullLiveData;
import ru.fitsme.android.presentation.common.livedata.NonNullMutableLiveData;
import ru.fitsme.android.presentation.fragments.base.BaseViewModel;
import ru.fitsme.android.presentation.fragments.signinup.entities.SignInUpState;
import ru.fitsme.android.presentation.main.AuthNavigation;
import timber.log.Timber;

@SuppressWarnings("Injectable")
public class SignUpViewModel extends BaseViewModel {

    @Inject
    AuthNavigation authNavigation;

    @Inject
    ISignInteractor signInteractor;

    private NonNullMutableLiveData<SignInUpState> fieldsStateLiveData = new NonNullMutableLiveData<>();

    public SignUpViewModel() {
        inject(this);
    }

    public void onSignUp(String login, String password) {
        fieldsStateLiveData.setValue(new SignInUpState(null, true));
        addDisposable(signInteractor.signUp(login, password)
                .subscribe(this::onSignInUpResult, this::onError));
    }

    private void onSignInUpResult(@NotNull SignInUpResult signInUpResult) {
        fieldsStateLiveData.setValue(new SignInUpState(signInUpResult, false));
        if (signInUpResult.isSuccess()) {
            authNavigation.goToMainItem();
        }
    }

    private void onError(Throwable throwable) {
        Timber.tag(getClass().getName()).e(throwable);
    }

    public NonNullLiveData<SignInUpState> getFieldsStateLiveData() {
        return fieldsStateLiveData;
    }
}
