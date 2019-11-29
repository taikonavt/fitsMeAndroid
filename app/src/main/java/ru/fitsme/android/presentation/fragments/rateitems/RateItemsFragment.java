package ru.fitsme.android.presentation.fragments.rateitems;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import javax.inject.Inject;

import ru.fitsme.android.R;
import ru.fitsme.android.databinding.FragmentRateItemsBinding;
import ru.fitsme.android.domain.interactors.clothes.IClothesInteractor;
import ru.fitsme.android.presentation.fragments.base.BaseFragment;
import ru.fitsme.android.presentation.fragments.base.ViewModelFactory;
import ru.fitsme.android.presentation.fragments.iteminfo.ClotheInfo;
import ru.fitsme.android.presentation.fragments.iteminfo.ItemInfoFragment;
import ru.fitsme.android.presentation.fragments.main.MainFragment;
import timber.log.Timber;

public class RateItemsFragment extends BaseFragment<RateItemsViewModel>
        implements BindingEventsClickListener,
        RateItemTouchListener.Callback,
        RateItemAnimation.Callback {

    private static final String KEY_ITEM_INFO_STATE = "state";

    @Inject
    IClothesInteractor clothesInteractor;

    private ItemInfoFragment currentFragment;
    private FragmentRateItemsBinding binding;
    private RateItemAnimation itemAnimation;
    private boolean isFullItemInfoState;
    private RateItemTouchListener rateItemTouchListener;
    private ClotheInfo currentClotheInfo;


    public static RateItemsFragment newInstance() {
        return new RateItemsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isFullItemInfoState = getArguments().getBoolean(KEY_ITEM_INFO_STATE);
        } else {
            isFullItemInfoState = false;
            Bundle bundle = new Bundle();
            bundle.putBoolean(KEY_ITEM_INFO_STATE, isFullItemInfoState);
            setArguments(bundle);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rate_items, container, false);
        binding.setBindingEvents(this);
        itemAnimation = new RateItemAnimation(this, binding);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = ViewModelProviders.of(this,
                new ViewModelFactory(clothesInteractor)).get(RateItemsViewModel.class);
        viewModel.init();

        viewModel.getClotheInfoLiveData()
                .observe(this, this::onChange);
    }

    @Override
    public void onBackPressed() {
        viewModel.onBackPressed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.clearDisposables();
    }

    private void onChange(ClotheInfo clotheInfo) {
        currentClotheInfo = clotheInfo;
        setClotheInfo(clotheInfo);
    }

    private void setClotheInfo(ClotheInfo clotheInfo) {
        int containerWidth = binding.fragmentRateItemsContainer.getWidth();
        int containerHeight = getContainerHeight();

        rateItemTouchListener = new RateItemTouchListener(this);
        currentFragment = ItemInfoFragment.newInstance(
                clotheInfo,
                isFullItemInfoState,
                containerHeight, containerWidth,
                rateItemTouchListener
        );

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_rate_items_container, currentFragment)
                .commit();
        resetContainerView();
    }


    private int getContainerHeight() {
        int containerHeight;
        if (isFullItemInfoState) {
            int px = binding.fragmentRateItemsButtonsGroup.getHeight();
            int bottomPx = getParentFragment() == null ? 0 : ((MainFragment) getParentFragment()).getBottomNavigationSize();
            containerHeight = binding.fragmentRateItemsContainer.getHeight() - px - bottomPx;
        } else {
            containerHeight = binding.fragmentRateItemsContainer.getHeight();
        }
        return containerHeight;
    }

    @Override
    public void onClickLikeItem() {
        startToLikeItem();
    }

    @Override
    public void onClickReturn() {
        viewModel.onReturnClicked(currentClotheInfo);
    }

    @Override
    public void onClickDislikeItem() {
        startToDislikeItem();
    }

    @Override
    public void onClickFilter() {
        Timber.d("onClickFilter()");
    }

    public void setFullItemInfoState(boolean b) {
        isFullItemInfoState = b;
        getArguments().putBoolean(KEY_ITEM_INFO_STATE, isFullItemInfoState);
        if (b){
            binding.fragmentRateItemsReturnBtn.setVisibility(View.INVISIBLE);
            binding.fragmentRateItemsFilterBtn.setVisibility(View.INVISIBLE);
            if (getParentFragment() != null) {
                ((MainFragment) getParentFragment()).showBottomNavigation(false);
            }
            setConstraintToFullState(true);
        } else {
            binding.fragmentRateItemsReturnBtn.setVisibility(View.VISIBLE);
            binding.fragmentRateItemsFilterBtn.setVisibility(View.VISIBLE);
            if (getParentFragment() != null) {
                ((MainFragment) getParentFragment()).showBottomNavigation(true);
            }
            setConstraintToFullState(false);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setConstraintToFullState(boolean b) {
        ConstraintSet set = new ConstraintSet();
        set.clone(binding.rateItemsLayout);
        if (b) {
            set.connect(R.id.fragment_rate_items_container, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        } else {
            set.connect(R.id.fragment_rate_items_container, ConstraintSet.BOTTOM, R.id.fragment_rate_items_buttons_group, ConstraintSet.TOP);
        }
        set.applyTo(binding.rateItemsLayout);
    }

    @Override
    public void maybeLikeItem(float alpha) {
        currentFragment.showYes(true, alpha);
    }

    @Override
    public void startToLikeItem() {
        currentFragment.showYes(true);
        itemAnimation.moveViewOutOfScreenToRight();
    }

    @Override
    public void maybeDislikeItem(float alpha) {
        currentFragment.showNo(true, alpha);
    }

    @Override
    public void startToDislikeItem() {
        currentFragment.showNo(true);
        itemAnimation.moveViewOutOfScreenToLeft();
    }

    @Override
    public void moveViewToXY(int deltaX, int deltaY) {
        itemAnimation.moveViewToXY(deltaX, deltaY);
    }

    @Override
    public void rotateView(float degrees) {
        itemAnimation.rotateView(degrees);
    }

    @Override
    public void resetContainerViewWithAnimation() {
        itemAnimation.resetContainerViewWithAnimation();
    }

    @Override
    public void resetContainerView() {
        itemAnimation.resetContainerView();
    }

    @Override
    public void likeItem() {
        if (currentFragment != null) {
            viewModel.likeClothesItem(currentClotheInfo, true);
        }
    }

    @Override
    public void dislikeItem() {
        if (currentFragment != null) {
            viewModel.likeClothesItem(currentClotheInfo, false);
        }
    }
}
