package ru.fitsme.android.presentation.fragments.iteminfo;

import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Constraints;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import javax.inject.Inject;

import ru.fitsme.android.R;
import ru.fitsme.android.app.App;
import ru.fitsme.android.databinding.FragmentItemInfoBinding;
import ru.fitsme.android.domain.entities.clothes.ClothesItem;
import ru.fitsme.android.domain.entities.clothes.LikedClothesItem;
import ru.fitsme.android.domain.entities.exceptions.user.UserException;
import ru.fitsme.android.domain.entities.favourites.FavouritesItem;
import ru.fitsme.android.domain.interactors.clothes.IClothesInteractor;
import ru.fitsme.android.presentation.fragments.base.BaseFragment;
import ru.fitsme.android.presentation.fragments.base.ViewModelFactory;
import ru.fitsme.android.presentation.fragments.main.MainFragment;
import ru.fitsme.android.presentation.fragments.rateitems.RateItemTouchListener;
import ru.fitsme.android.presentation.fragments.rateitems.RateItemsFragment;

public class ItemInfoFragment extends BaseFragment<ItemInfoViewModel>
        implements BindingEventsClickListener, ItemInfoTouchListener.Callback{

    @Inject
    IClothesInteractor clothesInteractor;

    private static ClotheInfo clotheInfo;
    private static boolean isDetailState;
    private static int containerHeight;
    private static int containerWidth;
    private FragmentItemInfoBinding binding;
    private ItemInfoPictureHelper pictureHelper;

    private static RateItemTouchListener rateItemTouchListener;

    public static ItemInfoFragment newInstance(ClotheInfo item, boolean isFullItemInfoState,
                                               int containerHeight, int containerWidth,
                                               RateItemTouchListener rateItemTouchListener) {
        ItemInfoFragment.rateItemTouchListener = rateItemTouchListener;
        ItemInfoFragment fragment = new ItemInfoFragment();

        ItemInfoFragment.clotheInfo = item;
        ItemInfoFragment.isDetailState = isFullItemInfoState;
        ItemInfoFragment.containerHeight = containerHeight;
        ItemInfoFragment.containerWidth = containerWidth;
        return fragment;
    }

    public static ItemInfoFragment newInstance(Object object) {
        ClothesItem clothesItem = (ClothesItem) object;
        ItemInfoFragment fragment = new ItemInfoFragment();

        ItemInfoFragment.clotheInfo = new ClotheInfo(clothesItem);
        ItemInfoFragment.isDetailState = true;
        ItemInfoFragment.containerHeight = 0;
        ItemInfoFragment.containerWidth = 0;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_info, container, false);
        binding.setBindingEvents(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setMargins();
        setFullState(isDetailState);

        viewModel = ViewModelProviders.of(this,
                new ViewModelFactory(clothesInteractor)).get(ItemInfoViewModel.class);
        if (savedInstanceState == null) {
            viewModel.init();
        }

        if (clotheInfo.getClothe() == null) {
            onError(clotheInfo.getError());
        } else if (clotheInfo.getClothe() instanceof ClothesItem) {
            onClothesItem((ClothesItem) clotheInfo.getClothe());
        } else if (clotheInfo.getClothe() instanceof LikedClothesItem) {
            onLikedClothesItem();
        } else {
            throw new TypeNotPresentException(clotheInfo.getClothe().toString(), null);
        }

        binding.itemInfoScrollView.setListener(rateItemTouchListener);
        binding.itemInfoScrollView.setListener(new ItemInfoTouchListener(this));
        setOnBrandNameTouchListener();
    }

    private void setMargins() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            float topMerge = -3.0f;

            Resources r = getResources();
            int px = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    topMerge,
                    r.getDisplayMetrics()
            );

            Constraints.LayoutParams params = new Constraints.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, px, 0, 0);
        }
    }

    private void onError(UserException error) {
        binding.itemInfoMessage.setText(error.getMessage());
    }

    private void onClothesItem(ClothesItem clothesItem) {
        binding.itemInfoMessage.setText(getString(R.string.loading));
        binding.itemInfoMessage.requestLayout();
        binding.itemInfoItemInfoCard.setVisibility(View.INVISIBLE);
        String brandName = clothesItem.getBrand();
        String name = clothesItem.getName();
        String description = clothesItem.getDescription();
        String url = clothesItem.getPics().get(0).getUrl();

        StringBuilder clotheContentStr = new StringBuilder();
        String itemsString = clothesItem.getMaterialPercentage().replaceAll("[{}]", "");
        for (String item : itemsString.split(",")) {
            String material = item.split(":")[0].replace("'", "").trim();
            String percent = item.split(":")[1].trim();
            clotheContentStr.append(percent).append("% ").append(material).append("\n");
        }

        binding.itemInfoBrandNameTv.setText(brandName);
        binding.itemInfoItemNameTv.setText(name);
        binding.itemInfoItemDescriptionTv.setText(description);
        binding.itemInfoItemContentTv.setText(clotheContentStr.toString());

        pictureHelper =
                new ItemInfoPictureHelper(this, binding, clothesItem, containerWidth, containerHeight);

    }

    private void onLikedClothesItem() {
        // TODO: 30.07.2019 Выполняется в случае отмены лайка. Не сделано еще
    }

    @Override
    public void onClickBrandName() {

    }

    private void setOnBrandNameTouchListener(){
        binding.itemInfoBrandNameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (binding.itemInfoBrandFieldDownArrow.getVisibility() == View.VISIBLE) {
                            ItemInfoFragment.this.setDetailState();
                        } else {
                            ItemInfoFragment.this.setSummaryState();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        v.performClick();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    public void showYes(boolean b, float alpha) {
        if (b) {
            binding.rateItemsYes.setAlpha(alpha);
            binding.rateItemsYes.setVisibility(View.VISIBLE);
            showNo(false);
        } else {
            binding.rateItemsYes.setVisibility(View.INVISIBLE);
        }
    }

    public void showYes(boolean b) {
        showYes(b, 1.0f);
    }

    public void showNo(boolean b, float alpha) {
        if (b) {
            binding.rateItemsNo.setVisibility(View.VISIBLE);
            binding.rateItemsNo.setAlpha(alpha);
            showYes(false);
        } else {
            binding.rateItemsNo.setVisibility(View.INVISIBLE);
        }
    }

    public void showNo(boolean b) {
        showNo(b, 1.0f);
    }

    private void setFullState(boolean b) {
        if (b) {
            setDetailState();
        } else {
            setSummaryState();
        }
    }

    private void setSummaryState() {
        isDetailState = false;

        binding.ivPhoto.getLayoutParams().height = FrameLayout.LayoutParams.WRAP_CONTENT;
        binding.ivPhoto.getLayoutParams().width = FrameLayout.LayoutParams.WRAP_CONTENT;
        binding.ivPhoto.requestLayout();
        binding.itemInfoItemInfoCard.getLayoutParams().width = FrameLayout.LayoutParams.WRAP_CONTENT;
        binding.itemInfoBrandFieldDownArrow.setVisibility(View.VISIBLE);
        binding.itemInfoBrandFieldUpArrow.setVisibility(View.INVISIBLE);
        binding.itemInfoItemDescriptionLayout.setVisibility(View.GONE);
        binding.itemInfoScrollView.setDetailState(false);
        if (getParentFragment() != null) {
            ((RateItemsFragment) getParentFragment()).setFullItemInfoState(false);
        }
        int paddingVal = App.getInstance().getResources().getDimensionPixelSize(R.dimen.item_info_card_padding);
        binding.itemInfoItemInfoContainer.setPadding(paddingVal, paddingVal, paddingVal, paddingVal);
        binding.itemInfoItemInfoCard.setRadius(App.getInstance().getResources().getDimension(R.dimen.item_info_card_radius));
        binding.itemInfoItemInfoCard.setCardElevation(App.getInstance().getResources().getDimension(R.dimen.items_info_elevation));
        binding.itemInfoBrandNameCard.setCardElevation(App.getInstance().getResources().getDimension(R.dimen.items_info_elevation));
    }

    private void setDetailState() {
        isDetailState = true;

        binding.itemInfoBrandFieldDownArrow.setVisibility(View.INVISIBLE);
        binding.itemInfoBrandFieldUpArrow.setVisibility(View.VISIBLE);
        binding.itemInfoItemDescriptionLayout.setVisibility(View.VISIBLE);
        binding.itemInfoScrollView.setDetailState(true);
        int paddingVal = 0;
        binding.itemInfoBrandNameCard.setCardElevation(0);
        binding.itemInfoItemInfoContainer.setPadding(paddingVal, paddingVal, paddingVal, paddingVal);
        binding.itemInfoItemInfoCard.setRadius(0);
        binding.itemInfoItemInfoCard.setCardElevation(0);
        binding.itemInfoItemInfoCard.getLayoutParams().width = FrameLayout.LayoutParams.MATCH_PARENT;
        binding.ivPhoto.getLayoutParams().height = FrameLayout.LayoutParams.WRAP_CONTENT;
        binding.ivPhoto.getLayoutParams().width = FrameLayout.LayoutParams.MATCH_PARENT;
        binding.ivPhoto.requestLayout();
        if (getParentFragment() != null) {
            if (getParentFragment() instanceof RateItemsFragment) {
                ((RateItemsFragment) getParentFragment()).setFullItemInfoState(true);
            } else if (getParentFragment() instanceof MainFragment) {
                binding.itemInfoBrandNameCard.setVisibility(View.GONE);
                binding.itemInfoBrandNameLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void nextPicture() {
        pictureHelper.setNextPicture();
    }

    @Override
    public void previousPicture() {
        pictureHelper.setPreviousPicture();
    }

    @Override
    public void onBackPressed() {
        viewModel.onBackPressed();
    }
}
