package ru.fitsme.android.presentation.fragments.cart;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PagedList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Single;
import ru.fitsme.android.domain.entities.order.OrderItem;
import ru.fitsme.android.domain.interactors.orders.IOrdersInteractor;
import ru.fitsme.android.presentation.fragments.base.BaseViewModel;

public class CartViewModel extends BaseViewModel {

    private final IOrdersInteractor ordersInteractor;

    public ObservableField<String> message;
    public ObservableInt totalPrice;

    public CartViewModel(@NotNull IOrdersInteractor ordersInteractor) {
        this.ordersInteractor = ordersInteractor;
    }

    public void init() {
        message = ordersInteractor.getMessage();
        totalPrice = ordersInteractor.getTotalPrice();
    }

    LiveData<PagedList<OrderItem>> getPageLiveData() {
        return ordersInteractor.getPagedListLiveData();
    }

    LiveData<Boolean> getCartIsEmpty(){
        return ordersInteractor.getCartIsEmpty();
    }

    public Single<OrderItem> removeItemFromOrder(int position) {
        return ordersInteractor.removeItemFromOrder(position);
    }

    Single<OrderItem> restoreItemToOrder(int position){
        return ordersInteractor.restoreItemToOrder(position);
    }

    boolean itemIsRemoved(int position) {
        return ordersInteractor.itemIsRemoved(position);
    }
}
