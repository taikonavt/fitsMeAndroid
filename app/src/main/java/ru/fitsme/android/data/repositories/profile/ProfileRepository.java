package ru.fitsme.android.data.repositories.profile;

import javax.inject.Inject;

import io.reactivex.Single;
import ru.fitsme.android.data.frameworks.retrofit.WebLoaderNetworkChecker;
import ru.fitsme.android.data.repositories.ErrorRepository;
import ru.fitsme.android.domain.boundaries.profile.IProfileRepository;
import ru.fitsme.android.domain.entities.exceptions.user.UserException;
import ru.fitsme.android.domain.entities.profile.Profile;

public class ProfileRepository implements IProfileRepository {

    private final WebLoaderNetworkChecker webLoader;

    @Inject
    ProfileRepository(WebLoaderNetworkChecker webLoader) {
        this.webLoader = webLoader;
    }

    @Override
    public Single<Profile> getProfile() {
        return Single.create(emitter -> webLoader.getProfile()
                .subscribe(profileOkResponse -> {
                    Profile profile = profileOkResponse.getResponse();
                    if (profile != null) {
                        emitter.onSuccess(profile);
                    } else {
                        UserException error = ErrorRepository.makeError(profileOkResponse.getError());
                        emitter.onError(error);
                    }
                }, emitter::onError));
    }

    @Override
    public Single<Profile> setProfile(Profile profile) {
        return Single.create(emitter -> webLoader.setProfile(profile)
                .subscribe(profileOkResponse -> {
                    Profile reply = profileOkResponse.getResponse();
                    if (reply != null) {
                        emitter.onSuccess(reply);
                    } else {
                        UserException error = ErrorRepository.makeError(profileOkResponse.getError());
                        emitter.onError(error);
                    }
                }, emitter::onError));
    }
}
