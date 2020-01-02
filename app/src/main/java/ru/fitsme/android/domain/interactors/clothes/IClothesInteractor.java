package ru.fitsme.android.domain.interactors.clothes;

import java.util.List;

import androidx.lifecycle.LiveData;

import ru.fitsme.android.domain.entities.clothes.FilterBrand;
import ru.fitsme.android.domain.entities.clothes.FilterColor;
import ru.fitsme.android.domain.entities.clothes.FilterProductName;
import ru.fitsme.android.domain.interactors.BaseInteractor;
import ru.fitsme.android.presentation.fragments.iteminfo.ClotheInfo;

public interface IClothesInteractor extends BaseInteractor {

    void updateClothesList();

    void setLikeToClothesItem(ClotheInfo clotheInfo, boolean liked);

    LiveData<ClotheInfo> getClotheInfoLiveData();

    void setPreviousClotheInfo(ClotheInfo current);

    LiveData<List<FilterProductName>> getProductNames();

    LiveData<List<FilterBrand>> getBrands();

    LiveData<List<FilterColor>> getColors();

    void setFilterProductName(FilterProductName filterProductName);

    void setFilterBrand(FilterBrand filterBrand);

    void setFilterColor(FilterColor filterColor);
}
