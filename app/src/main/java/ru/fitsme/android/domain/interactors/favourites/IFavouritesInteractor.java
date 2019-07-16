package ru.fitsme.android.domain.interactors.favourites;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.Single;
import ru.fitsme.android.data.repositories.favourites.entity.FavouritesPage;
import ru.fitsme.android.domain.entities.favourites.FavouritesItem;
import ru.fitsme.android.domain.interactors.BaseInteractor;

public interface IFavouritesInteractor extends BaseInteractor {

//    @NonNull
//    Single<Integer> getClothe();
//
//    @NonNull
//    Single<FavouritesItem> getSingleFavouritesItem(int index);
//
//    @NonNull
//    Single<FavouritesPage> getSingleFavouritesPage(int page);
//
//    @NonNull
//    Completable restoreItemToFavourites(int index);

    @NonNull
    Completable addFavouritesItemToCart(int position);

    @NonNull
    Completable deleteFavouriteItem(Integer position);

    LiveData<PagedList<FavouritesItem>> getPagedListLiveData();
}
