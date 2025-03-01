package ru.fitsme.android.data.repositories.clothes;

import android.annotation.SuppressLint;
import android.util.SparseArray;

import androidx.lifecycle.LiveData;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import ru.fitsme.android.R;
import ru.fitsme.android.app.App;
import ru.fitsme.android.data.frameworks.retrofit.WebLoaderNetworkChecker;
import ru.fitsme.android.data.frameworks.room.RoomBrand;
import ru.fitsme.android.data.frameworks.room.RoomColor;
import ru.fitsme.android.data.frameworks.room.RoomProductName;
import ru.fitsme.android.data.frameworks.room.dao.BrandsDao;
import ru.fitsme.android.data.frameworks.room.dao.ColorsDao;
import ru.fitsme.android.data.frameworks.room.dao.ProductNamesDao;
import ru.fitsme.android.data.frameworks.room.db.AppDatabase;
import ru.fitsme.android.data.frameworks.sharedpreferences.ISettingsStorage;
import ru.fitsme.android.data.repositories.ErrorRepository;
import ru.fitsme.android.data.repositories.clothes.entity.ClotheSizeType;
import ru.fitsme.android.data.repositories.clothes.entity.ClothesPage;
import ru.fitsme.android.data.repositories.clothes.entity.RepoClotheBrand;
import ru.fitsme.android.data.repositories.clothes.entity.RepoClotheColor;
import ru.fitsme.android.data.repositories.clothes.entity.RepoClotheProductName;
import ru.fitsme.android.domain.boundaries.clothes.IClothesRepository;
import ru.fitsme.android.domain.entities.clothes.ClotheSize;
import ru.fitsme.android.domain.entities.clothes.ClothesItem;
import ru.fitsme.android.domain.entities.clothes.FilterBrand;
import ru.fitsme.android.domain.entities.clothes.FilterColor;
import ru.fitsme.android.domain.entities.clothes.FilterProductName;
import ru.fitsme.android.domain.entities.clothes.LikedClothesItem;
import ru.fitsme.android.domain.entities.exceptions.user.UserException;
import ru.fitsme.android.presentation.fragments.iteminfo.ClotheInfo;
import timber.log.Timber;

public class ClothesRepository implements IClothesRepository {

    private final WebLoaderNetworkChecker webLoader;
    private final ISettingsStorage storage;
    private Scheduler workThread;
    private Scheduler mainThread;

    @Inject
    ClothesRepository(WebLoaderNetworkChecker webLoader,
                      ISettingsStorage storage,
                      @Named("work") Scheduler workThread,
                      @Named("main") Scheduler mainThread) {
        this.webLoader = webLoader;
        this.storage = storage;
        this.workThread = workThread;
        this.mainThread = mainThread;
    }

    @Override
    public Single<ClotheInfo> likeItem(ClotheInfo clotheInfo, boolean liked) {
        ClothesItem clothesItem;
        if (clotheInfo.getClothe() instanceof ClothesItem) {
            clothesItem = (ClothesItem) clotheInfo.getClothe();
        } else if (clotheInfo.getClothe() instanceof LikedClothesItem) {
            LikedClothesItem likedClothesItem = (LikedClothesItem) clotheInfo.getClothe();
            clothesItem = likedClothesItem.getClothe();
        } else {
            throw new TypeNotPresentException(clotheInfo.toString(), null);
        }
        return Single.create(emitter -> webLoader.likeItem(clothesItem, liked)
                .subscribe(response -> {
                    LikedClothesItem item = response.getResponse();
                    if (item != null) {
                        emitter.onSuccess(new ClotheInfo<>(item));
                    } else {
                        emitter.onSuccess(new ClotheInfo(response.getError()));
                    }
                }, emitter::onError));
    }

    @Override
    public Single<Boolean> returnItemFromViewed(int clotheId) {
        return Single.create(emitter -> webLoader.returnItemFromViewed(clotheId)
                .subscribe(response -> {
                    emitter.onSuccess(response.isSuccessful());
                }, error -> {
                    Timber.e(error);
                    emitter.onError(error);
                }));
    }

    @Override
    public Single<List<ClotheInfo>> getClotheList() {
        int page = 1;
        return Single.create(emitter -> webLoader.getClothesPage(page)
                .subscribe(response -> {
                    ClothesPage clothePage = response.getResponse();
                    List<ClotheInfo> clotheInfoList = new LinkedList<>();
                    if (clothePage != null) {
                        List<ClothesItem> clothesItemList = clothePage.getItems();
                        if (clothesItemList.size() == 0) {
                            ClotheInfo clotheInfo = new ClotheInfo(new UserException(
                                    App.getInstance().getString(R.string.end_of_not_viewed_list)));
                            clotheInfoList.add(clotheInfo);
                            emitter.onSuccess(clotheInfoList);
                        } else {
                            for (int i = 0; i < clothesItemList.size(); i++) {
                                ClothesItem item = clothesItemList.get(i);
                                item.getPics().get(0).downloadPic();
                                clotheInfoList.add(new ClotheInfo<ClothesItem>(item));
                            }
                        }
                    } else {
                        UserException error = ErrorRepository.makeError(response.getError());
                        clotheInfoList.add(new ClotheInfo(error));
                    }
                    emitter.onSuccess(clotheInfoList);
                }, emitter::onError));
    }


    @Override
    public Single<SparseArray<ClotheSize>> getSizes() {
        return Single.create(emitter -> webLoader.getSizes()
                .subscribe(sizesOkResponse -> {
                    List<ClotheSize> clotheSizes = sizesOkResponse.getResponse();
                    if (clotheSizes != null) {
                        SparseArray<ClotheSize> sparseArray = new SparseArray<>();
                        for (int i = 0; i < clotheSizes.size(); i++) {
                            sparseArray.put(clotheSizes.get(i).getId(), clotheSizes.get(i));
                        }
                        emitter.onSuccess(sparseArray);
                    } else {
                        UserException error = ErrorRepository.makeError(sizesOkResponse.getError());
                        emitter.onError(error);
                    }
                }, emitter::onError));
    }

    @Override
    public ClotheSizeType getSettingTopClothesSizeType() {
        return storage.getTopSizeType();
    }

    @Override
    public ClotheSizeType getSettingsBottomClothesSizeType() {
        return storage.getBottomSizeType();
    }

    @Override
    public void setSettingsTopClothesSizeType(ClotheSizeType clothesSizeType) {
        storage.setTopSizeType(clothesSizeType);
    }

    @Override
    public void setSettingsBottomClothesSizeType(ClotheSizeType clothesSizeType) {
        storage.setBottomSizeType(clothesSizeType);
    }

    @SuppressLint("CheckResult")
    @Override
    public void updateClotheBrandList() {
        webLoader.getBrandList()
                .subscribe(listOkResponse -> {
                    List<RepoClotheBrand> clotheBrands = listOkResponse.getResponse();
                    if (clotheBrands != null) {
                        BrandsDao brandsDao = AppDatabase.getInstance().getBrandsDao();
                        brandsDao.getSingleBrandsList()
                                .subscribe(roomBrands -> {
                                    for (int i = 0; i < clotheBrands.size(); i++) {
                                        RepoClotheBrand brand = clotheBrands.get(i);
                                        boolean isUpdated = false;
                                        for (int j = 0; j < roomBrands.size(); j++) {
                                            RoomBrand roomBrand = roomBrands.get(j);
                                            if (brand.getId() == roomBrand.getId()) {
                                                isUpdated = true;
                                                brandsDao.update(new RoomBrand(roomBrand.getId(), roomBrand.getTitle(), roomBrand.isChecked(), true));
                                                break;
                                            }
                                        }
                                        if (!isUpdated) {
                                            brandsDao.insert(new RoomBrand(brand.getId(), brand.getTitle(), false, true));
                                        }
                                    }
                                    brandsDao.clearNotUpdatedBrands();
                                    setRoomBrandsNotUpdated();
                                });
                    }
                }, Timber::e);
    }

    @SuppressLint("CheckResult")
    private void setRoomBrandsNotUpdated() {
        BrandsDao brandsDao = AppDatabase.getInstance().getBrandsDao();
        brandsDao.getSingleBrandsList()
                .subscribe(roomBrandList -> {
                    for (int i = 0; i < roomBrandList.size(); i++) {
                        RoomBrand brand = roomBrandList.get(i);
                        brandsDao.update(new RoomBrand(brand.getId(), brand.getTitle(), brand.isChecked(), false));
                    }
                });
    }

    @Override
    public LiveData<List<RoomBrand>> getBrandNames() {
        return AppDatabase.getInstance().getBrandsDao().getBrandsLiveData();
    }

    @SuppressLint("CheckResult")
    @Override
    public void updateClotheColorList() {
        webLoader.getColorList()
                .subscribe(listOkResponse -> {
                    List<RepoClotheColor> clotheColorList = listOkResponse.getResponse();
                    if (clotheColorList != null) {
                        ColorsDao colorsDao = AppDatabase.getInstance().getColorsDao();
                        colorsDao.getSingleColorsList()
                                .subscribe(roomColors -> {
                                    for (int i = 0; i < clotheColorList.size(); i++) {
                                        RepoClotheColor color = clotheColorList.get(i);
                                        boolean isUpdated = false;
                                        for (int j = 0; j < roomColors.size(); j++) {
                                            RoomColor roomColor = roomColors.get(j);
                                            if (color.getId() == roomColor.getId()) {
                                                isUpdated = true;
                                                colorsDao.update(new RoomColor(roomColor.getId(), roomColor.getColorName(), roomColor.getColorHex(), roomColor.isChecked(), true));
                                                break;
                                            }
                                        }
                                        if (!isUpdated) {
                                            colorsDao.insert(new RoomColor(color.getId(), color.getColorName(), color.getColorHex(), false, true));
                                        }
                                    }
                                    colorsDao.clearNotUpdatedColors();
                                    setRoomColorsNotUpdated();
                                });
                    }
                }, Timber::e);
    }

    @SuppressLint("CheckResult")
    private void setRoomColorsNotUpdated() {
        ColorsDao colorsDao = AppDatabase.getInstance().getColorsDao();
        colorsDao.getSingleColorsList()
                .subscribe(roomColorsList -> {
                    for (int i = 0; i < roomColorsList.size(); i++) {
                        RoomColor roomColor = roomColorsList.get(i);
                        colorsDao.update(new RoomColor(roomColor.getId(), roomColor.getColorName(), roomColor.getColorHex(), roomColor.isChecked(), false));
                    }
                });
    }

    @Override
    public LiveData<List<RoomColor>> getClotheColors() {
        return AppDatabase.getInstance().getColorsDao().getColorsLiveData();
    }

    @SuppressLint("CheckResult")
    @Override
    public void updateProductNameList() {
        webLoader.getProductNameList()
                .subscribe(listOkResponse -> {
                    List<RepoClotheProductName> clotheProductNameList = listOkResponse.getResponse();
                    if (clotheProductNameList != null) {
                        ProductNamesDao productNamesDao = AppDatabase.getInstance().getProductNamesDao();
                        productNamesDao.getSingleProductNamesList()
                                .subscribe(roomProductNames -> {
                                    for (int i = 0; i < clotheProductNameList.size(); i++) {
                                        RepoClotheProductName productName = clotheProductNameList.get(i);
                                        boolean isUpdated = false;
                                        for (int j = 0; j < roomProductNames.size(); j++) {
                                            RoomProductName roomProductName = roomProductNames.get(j);
                                            if (productName.getId() == roomProductName.getId()) {
                                                isUpdated = true;
                                                productNamesDao.update(new RoomProductName(
                                                        roomProductName.getId(), roomProductName.getTitle(), roomProductName.getType(), roomProductName.isChecked(), true));
                                                break;
                                            }
                                        }
                                        if (!isUpdated) {
                                            productNamesDao.insert(new RoomProductName(productName.getId(), productName.getTitle(), productName.getType(), false, true));
                                        }
                                    }
                                    productNamesDao.clearNotUpdatedProductNames();
                                    setRoomProductNamesNotUpdated();
                                });
                    }
                }, Timber::e);
    }

    @SuppressLint("CheckResult")
    private void setRoomProductNamesNotUpdated() {
        ProductNamesDao productNamesDao = AppDatabase.getInstance().getProductNamesDao();
        productNamesDao.getSingleProductNamesList()
                .subscribe(roomProductNames -> {
                    for (int i = 0; i < roomProductNames.size(); i++) {
                        RoomProductName roomProductName = roomProductNames.get(i);
                        productNamesDao.update(new RoomProductName(roomProductName.getId(), roomProductName.getTitle(), roomProductName.getType(), roomProductName.isChecked(), false));
                    }
                });
    }

    @Override
    public LiveData<List<RoomProductName>> getClotheProductName() {
        return AppDatabase.getInstance().getProductNamesDao().getProductNamesLiveData();
    }

    @Override
    public void updateProductName(FilterProductName filterProductName) {
        Completable.create(emitter -> {
            ProductNamesDao productNamesDao = AppDatabase.getInstance().getProductNamesDao();
            productNamesDao.update(
                    new RoomProductName(filterProductName.getId(), filterProductName.getTitle(),
                            filterProductName.getType(), filterProductName.isChecked(), false)
            );
            emitter.onComplete();
        })
                .subscribeOn(workThread)
                .subscribe();
    }

    @Override
    public void updateClotheBrand(FilterBrand filterBrand) {
        Completable.create(emitter -> {
            BrandsDao brandsDao = AppDatabase.getInstance().getBrandsDao();
            brandsDao.update(
                    new RoomBrand(filterBrand.getId(), filterBrand.getTitle(),
                            filterBrand.isChecked(), false)
            );
            emitter.onComplete();
        })
                .subscribeOn(workThread)
                .subscribe();
    }

    @Override
    public void updateClotheColor(FilterColor filterColor) {
        Completable.create(emitter -> {
            ColorsDao colorsDao = AppDatabase.getInstance().getColorsDao();
            colorsDao.update(new RoomColor(filterColor.getId(), filterColor.getTitle(),
                    filterColor.getColorHex(), filterColor.isChecked(), false));
            emitter.onComplete();
        })
                .subscribeOn(workThread)
                .subscribe();
    }

    @Override
    public void resetCheckedFilters() {
        Completable.create(emitter -> {
            resetProductNameFilters();
            resetBrandFilters();
            resetColorFilters();
        })
                .subscribeOn(workThread)
                .subscribe();
    }

    @SuppressLint("CheckResult")
    @Override
    public Single<Boolean> isFiltersChecked() {
        return Single.create(emitter -> {
            ProductNamesDao productNamesDao = AppDatabase.getInstance().getProductNamesDao();
            BrandsDao brandsDao = AppDatabase.getInstance().getBrandsDao();
            ColorsDao colorsDao = AppDatabase.getInstance().getColorsDao();
            productNamesDao.getCheckedFilters().subscribeOn(workThread).subscribe(roomProductNames -> {
                if (roomProductNames.size() > 0) {
                    emitter.onSuccess(true);
                } else {
                    brandsDao.getCheckedFilters().subscribe(roomBrands -> {
                        if (roomBrands.size() > 0) {
                            emitter.onSuccess(true);
                        } else {
                            colorsDao.getCheckedColors().subscribe(roomColors -> {
                                if (roomColors.size() > 0) {
                                    emitter.onSuccess(true);
                                } else {
                                    emitter.onSuccess(false);
                                }
                            });
                        }
                    });
                }
            });
        });
    }

    @Override
    public Boolean getIsNeedShowSizeDialogTop() {
        return storage.getIsNeedShowSizeDialogForRateItemsTop();
    }

    @Override
    public void setIsNeedShowSizeDialogTop(Boolean flag) {
        storage.setIsNeedShowSizeDialogForRateItemsTop(flag);
    }

    @Override
    public Boolean getIsNeedShowSizeDialogBottom() {
        return storage.getIsNeedShowSizeDialogForRateItemsBottom();
    }

    @Override
    public void setIsNeedShowSizeDialogBottom(Boolean flag) {
        storage.setIsNeedShowSizeDialogForRateItemsBottom(flag);
    }

    @SuppressLint("CheckResult")
    private void resetColorFilters() {
        ColorsDao colorsDao = AppDatabase.getInstance().getColorsDao();
        colorsDao.getCheckedColors()
                .subscribe(roomColors -> {
                    for (int i = 0; i < roomColors.size(); i++) {
                        roomColors.get(i).setChecked(false);
                        colorsDao.update(roomColors.get(i));
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void resetBrandFilters() {
        BrandsDao brandsDao = AppDatabase.getInstance().getBrandsDao();
        brandsDao.getCheckedFilters()
                .subscribe(roomBrands -> {
                    for (int i = 0; i < roomBrands.size(); i++) {
                        roomBrands.get(i).setChecked(false);
                        brandsDao.update(roomBrands.get(i));
                    }
                });

    }

    @SuppressLint("CheckResult")
    private void resetProductNameFilters() {
        ProductNamesDao productNamesDao = AppDatabase.getInstance().getProductNamesDao();
        productNamesDao.getCheckedFilters()
                .subscribe(roomProductNames -> {
                    for (int i = 0; i < roomProductNames.size(); i++) {
                        roomProductNames.get(i).setChecked(false);
                        productNamesDao.update(roomProductNames.get(i));
                    }
                });
    }
}
