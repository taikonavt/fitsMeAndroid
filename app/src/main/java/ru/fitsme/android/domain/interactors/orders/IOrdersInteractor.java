package ru.fitsme.android.domain.interactors.orders;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;


import io.reactivex.Completable;
import io.reactivex.Single;
import ru.fitsme.android.domain.entities.order.Order;
import ru.fitsme.android.domain.entities.order.OrderItem;
import ru.fitsme.android.utils.OrderStatus;

public interface IOrdersInteractor {

    @NonNull
    Single<Order> getSingleOrder(int page);

    @NonNull
    Completable removeItemFromOrder(int index);

    @NonNull
    Completable restoreItemToOrder(int index);

    @NonNull
    Completable makeOrder(String phoneNumber, String street, String houseNumber, String apartment, OrderStatus orderStatus);

    LiveData<PagedList<OrderItem>> getPagedListLiveData();
}
