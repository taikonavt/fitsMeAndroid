package ru.fitsme.android.presentation.fragments.returns.processing.four;

import org.jetbrains.annotations.NotNull;

import androidx.databinding.ObservableBoolean;
import ru.fitsme.android.data.frameworks.retrofit.entities.ReturnsPaymentRequest;
import ru.fitsme.android.domain.entities.returns.ReturnsOrderItem;
import ru.fitsme.android.domain.interactors.returns.IReturnsInteractor;
import ru.fitsme.android.presentation.fragments.base.BaseViewModel;
import ru.fitsme.android.utils.OrderStatus;
import timber.log.Timber;

public class IndicateNumberReturnViewModel extends BaseViewModel {

    private final IReturnsInteractor returnsInteractor;

    public ObservableBoolean isLoading = new ObservableBoolean(true);

    public IndicateNumberReturnViewModel(@NotNull IReturnsInteractor returnsInteractor) {
        this.returnsInteractor = returnsInteractor;
        inject(this);
    }

    void init() {
        isLoading.set(false);
    }

    public void goToReturnsBillingInfo(String indicationNumber, int returnId) {
        addDisposable(returnsInteractor.changeReturnsPayment(
                new ReturnsPaymentRequest(
                        returnId, indicationNumber, null, OrderStatus.FM.toString()
                ))
                .subscribe(this::onSuccess, this::onError)
        );
    }

    private void onError(Throwable throwable) {
        Timber.d(throwable);
    }

    private void onSuccess(ReturnsOrderItem returnsOrder) {
        navigation.goToReturnsBillingInfo(returnsOrder.getId());
    }

    public void backToReturnsChooseItems() {
        navigation.backToReturnsChooseItems();
    }

    @Override
    public void onBackPressed() {
        navigation.goBack();
    }
}
