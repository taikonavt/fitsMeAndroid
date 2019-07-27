package ru.fitsme.android.presentation.fragments.iteminfo;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import javax.inject.Inject;

import ru.fitsme.android.R;
import ru.fitsme.android.app.App;
import ru.fitsme.android.databinding.FragmentItemInfoBinding;
import ru.fitsme.android.domain.interactors.clothes.IClothesInteractor;
import ru.fitsme.android.presentation.fragments.base.BaseFragment;
import ru.fitsme.android.presentation.fragments.base.ViewModelFactory;
import ru.fitsme.android.presentation.fragments.rateitems.RateItemsFragment;

public class ItemInfoFragment extends BaseFragment<ItemInfoViewModel>
            implements BindingEventsClickListener{

    @Inject
    IClothesInteractor clothesInteractor;

    private static final String KEY_INDEX = "indexKey";
    private static final String KEY_ITEM_INFO_STATE = "state";

    private int index;
    private static boolean isFullState;
    private ItemInfoState.State state;
    private FragmentItemInfoBinding binding;

    public static ItemInfoFragment newInstance(int index, boolean isFullItemInfoState) {
        ItemInfoFragment fragment = new ItemInfoFragment();

        isFullState = isFullItemInfoState;
        Bundle args = new Bundle();
        args.putInt(KEY_INDEX, index);
        args.putBoolean(KEY_ITEM_INFO_STATE, isFullItemInfoState);
        fragment.setArguments(args);

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

        if (getArguments() != null){
            index = getArguments().getInt(KEY_INDEX);
            isFullState = getArguments().getBoolean(KEY_ITEM_INFO_STATE);
            setFullState(isFullState);
        }

        viewModel = ViewModelProviders.of(this,
                new ViewModelFactory(clothesInteractor, index)).get(ItemInfoViewModel.class);
        if (savedInstanceState == null) {
            viewModel.init();
        }

        viewModel.getItemLiveData()
                .observe(this, this::onItem);
    }

    private void onItem(ItemInfoState itemInfoState) {
        state = itemInfoState.getState();

        switch (state) {
            case ERROR:
                binding.tvIndex.setText(getString(R.string.item_info_load_error));
                break;
            case LOADING:
                binding.tvIndex.setText(getString(R.string.item_info_load_in_process));
                break;
            case OK:
                binding.tvIndex.setText(getString(R.string.item_info_load_in_process));
                String brandName = itemInfoState.getClothesItem().getBrandName();
                String name = itemInfoState.getClothesItem().getName();
                String description = itemInfoState.getClothesItem().getDescription();
                List<String> contentList = itemInfoState.getClothesItem().getMaterial();
                String content = contentList.toString();
                String url = itemInfoState.getClothesItem().getImageUrl();

                binding.itemInfoBrandNameTv.setText(brandName);
                binding.itemInfoItemNameTv.setText(name);
                binding.itemInfoItemDescriptionTv.setText(description);
                binding.itemInfoItemContentTv.setText(content);
                Glide.with(getContext())
                        .load(url)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                binding.tvIndex.setText(getString(R.string.item_info_load_error));
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                binding.tvIndex.setText(null);
                                return false;
                            }
                        })
                        .into(binding.ivPhoto);
                break;
        }
    }

    public boolean isActive() {
        return state == ItemInfoState.State.OK;
    }

    @Override
    public void onClickBrandName() {
        if(binding.itemInfoBrandFieldDownArrow.getVisibility() == View.VISIBLE){
            setFullState(true);
        } else {
            setFullState(false);
        }
    }

    public void showYes(boolean b){
        if (b){
            binding.rateItemsYes.setVisibility(View.VISIBLE);
            showNo(false);
        } else {
            binding.rateItemsYes.setVisibility(View.INVISIBLE);
        }
    }

    public void showNo(boolean b){
        if (b){
            binding.rateItemsNo.setVisibility(View.VISIBLE);
            showYes(false);
        } else {
            binding.rateItemsNo.setVisibility(View.INVISIBLE);
        }
    }

    private void setFullState(boolean b){
        if (b){
            isFullState = true;
            getArguments().putBoolean(KEY_ITEM_INFO_STATE, true);
            binding.itemInfoBrandFieldDownArrow.setVisibility(View.INVISIBLE);
            binding.itemInfoBrandFieldUpArrow.setVisibility(View.VISIBLE);
            binding.itemInfoItemDescriptionLayout.setVisibility(View.VISIBLE);
            ((RateItemsFragment) getParentFragment()).setFullItemInfoState(true);
            binding.itemInfoItemInfoContainer.setPadding(0,0,0,0);
            binding.itemInfoItemInfoCard.setRadius(0);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                binding.itemInfoItemInfoCard.setElevation(0);
                binding.itemInfoBrandNameCard.setElevation(0);
            }
        } else {
            isFullState = false;
            getArguments().putBoolean(KEY_ITEM_INFO_STATE, false);
            binding.itemInfoBrandFieldDownArrow.setVisibility(View.VISIBLE);
            binding.itemInfoBrandFieldUpArrow.setVisibility(View.INVISIBLE);
            binding.itemInfoItemDescriptionLayout.setVisibility(View.GONE);
            ((RateItemsFragment) getParentFragment()).setFullItemInfoState(false);
            int paddingVal = App.getInstance().getResources().getDimensionPixelSize(R.dimen.item_info_card_padding);
            binding.itemInfoItemInfoContainer.setPadding(paddingVal, paddingVal, paddingVal, paddingVal);
            binding.itemInfoItemInfoCard.setRadius(App.getInstance().getResources().getDimension(R.dimen.items_info_elevation));
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                binding.itemInfoItemInfoCard.setElevation(App.getInstance().getResources().getDimension(R.dimen.items_info_elevation));
                binding.itemInfoBrandNameCard.setElevation(App.getInstance().getResources().getDimension(R.dimen.items_info_elevation));
            }
        }
    }
}
