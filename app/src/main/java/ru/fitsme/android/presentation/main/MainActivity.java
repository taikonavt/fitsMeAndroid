package ru.fitsme.android.presentation.main;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import javax.inject.Inject;

import ru.fitsme.android.R;
import ru.fitsme.android.app.App;
import ru.fitsme.android.app.Navigation;
import ru.fitsme.android.presentation.fragments.signinup.view.SignInFragment;
import ru.fitsme.android.presentation.fragments.signinup.view.SignInUpFragment;
import ru.fitsme.android.presentation.fragments.signinup.view.SignUpFragment;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.android.SupportFragmentNavigator;

import static ru.fitsme.android.presentation.main.MainViewModel.NAV_DEBUG;
import static ru.fitsme.android.presentation.main.MainViewModel.NAV_SIGN_IN;
import static ru.fitsme.android.presentation.main.MainViewModel.NAV_SIGN_IN_UP;
import static ru.fitsme.android.presentation.main.MainViewModel.NAV_SIGN_UP;

public class MainActivity extends AppCompatActivity {

    @Inject
    Navigation navigation;
    private Navigator navigator = getFragmentNavigator();

    public MainActivity() {
        App.getInstance().getDi().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainViewModel mainViewModel = ViewModelProviders.of(this)
                .get(MainViewModel.class);
    }

    @Override
    protected void onResume() {
        super.onResume();

        navigation.getNavigatorHolder().setNavigator(navigator);
    }

    @Override
    protected void onPause() {
        super.onPause();

        navigation.getNavigatorHolder().removeNavigator();
    }

    @NonNull
    private SupportFragmentNavigator getFragmentNavigator() {
        return new SupportFragmentNavigator(getSupportFragmentManager(), R.id.fragment_container) {
            @Override
            protected Fragment createFragment(String screenKey, Object data) {
                switch (screenKey) {
                    case NAV_SIGN_IN_UP:
                        return SignInUpFragment.newInstance();
                    case NAV_SIGN_UP:
                        return SignUpFragment.newInstance();
                    case NAV_SIGN_IN:
                        return SignInFragment.newInstance();
                    case NAV_DEBUG:
                        Toast.makeText(MainActivity.this, "Auth success",
                                Toast.LENGTH_LONG).show();
                        break;
                }
                throw new RuntimeException("Unknown screen key");
            }

            @Override
            protected void showSystemMessage(String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void exit() {
                finish();
            }
        };
    }
}
