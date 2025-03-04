package ru.fitsme.android.presentation.fragments.returns.processing.six;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

import ru.fitsme.android.data.frameworks.retrofit.entities.ReturnsPaymentRequest;
import ru.fitsme.android.domain.entities.returns.ReturnsOrder;
import ru.fitsme.android.domain.entities.returns.ReturnsOrderItem;
import ru.fitsme.android.domain.interactors.returns.IReturnsInteractor;
import ru.fitsme.android.presentation.fragments.base.BaseViewModel;
import ru.fitsme.android.utils.OrderStatus;
import timber.log.Timber;

public class VerifyDataReturnViewModel extends BaseViewModel {

    @Inject
    IReturnsInteractor returnsInteractor;

    private int returnId = 0;

    public ObservableBoolean isLoading = new ObservableBoolean(true);
    private MutableLiveData<ReturnsOrder> returnsOrderLiveData = new MutableLiveData<>();

    public VerifyDataReturnViewModel() {
        inject(this);
    }

    public MutableLiveData<ReturnsOrder> getReturnsOrderLiveData() {
        return returnsOrderLiveData;
    }

    void init(int returnId) {
        isLoading.set(true);
        this.returnId = returnId;
        addDisposable(returnsInteractor.getReturnById(returnId)
                .subscribe(this::onSuccess, this::onError));
    }

    private void onSuccess(ReturnsOrder returnsOrder) {
        isLoading.set(false);
        returnsOrderLiveData.setValue(returnsOrder);
    }

    private void onError(Throwable throwable) {
        isLoading.set(false);
        Timber.d(throwable);
    }

    public void sendReturnOrder(int returnId) {
        addDisposable(returnsInteractor.changeReturnsPayment(
                new ReturnsPaymentRequest(returnId, null, null, OrderStatus.ISU.toString())
        ).subscribe(this::onSuccessConfirm, this::onError));
    }

    private void onSuccessConfirm(ReturnsOrderItem returnsOrderItem) {
        navigation.goBack();
    }

    @Override
    public void onBackPressed() {
        navigation.goToReturnsBillingInfoWithReplace(returnId);
    }
}
