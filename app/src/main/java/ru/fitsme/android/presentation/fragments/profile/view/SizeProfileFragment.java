package ru.fitsme.android.presentation.fragments.profile.view;

import android.view.View;
import android.widget.TableLayout;

import ru.fitsme.android.R;
import ru.fitsme.android.databinding.FragmentProfileChangeSizeBinding;
import ru.fitsme.android.presentation.common.listener.BackClickListener;
import ru.fitsme.android.presentation.fragments.base.BaseFragment;
import ru.fitsme.android.presentation.fragments.main.MainFragment;
import ru.fitsme.android.presentation.fragments.profile.events.SizeProfileBindingEvents;
import ru.fitsme.android.presentation.fragments.profile.viewmodel.SizeProfileViewModel;

public class SizeProfileFragment extends BaseFragment<SizeProfileViewModel>
        implements SizeProfileBindingEvents, SizeObserver.Callback, BackClickListener {

    private FragmentProfileChangeSizeBinding binding;
    private static final int TOP_TAG = 1;
    private SizeObserver topSizeObserver = new SizeObserver(this, TOP_TAG);
    private int lastSavedTopSize = -1;
    private static final int BOTTOM_TAG = 2;
    private SizeObserver bottomSizeObserver = new SizeObserver(this, BOTTOM_TAG);
    private int lastSavedBottomSize = -1;

    public static SizeProfileFragment newInstance() {
        return new SizeProfileFragment();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_profile_change_size;
    }

    @Override
    protected void afterCreateView(View view) {
        binding = FragmentProfileChangeSizeBinding.bind(view);
        binding.setBindingEvents(this);
        binding.setViewModel(viewModel);
        binding.appBar.setBackClickListener(this);
        binding.appBar.setTitle(getString(R.string.fragment_change_size_title));
        setUp();
    }

    @Override
    public void goBack() {
        viewModel.onBackPressed();
    }

    @Override
    public void onSizeValueSelected(int tag, int id) {
        switch (tag) {
            case TOP_TAG: {
                viewModel.onTopSizeValueSelected(id);
                break;
            }
            case BOTTOM_TAG: {
                viewModel.onBottomSizeValueSelected(id);
                break;
            }
        }
    }

    private void setUp() {
        if (getParentFragment() != null) {
            ((MainFragment) getParentFragment()).hideBottomNavbar();
        }
        setTopSizeCheckers();
        setBottomSizeCheckers();
    }

    private void setBottomSizeCheckers() {
        viewModel.getBottomSizeArray().observe(this, list -> {
            TableLayout tableLayout = binding.fragmentProfileBottomSizeLayout.bottomSizeProfileBottomSizesTable;
            if (getContext() != null) {
                TableFiller.fillButtons(getContext(), bottomSizeObserver, list, tableLayout);
            }
            if (viewModel.getCurrentBottomSizeIndex().get() != -1) {
                lastSavedBottomSize = viewModel.getCurrentBottomSizeIndex().get();
                bottomSizeObserver.setCheckedSizeIndex(lastSavedBottomSize);
                bottomSizeObserver.setState(lastSavedBottomSize, true);
            }
        });
    }

    private void setTopSizeCheckers() {
        viewModel.getTopSizeArray().observe(this, list -> {
            TableLayout tableLayout = binding.fragmentProfileTopSizeLayout.topSizeProfileSizesTable;
            if (getContext() != null) {
                TableFiller.fillButtons(getContext(), topSizeObserver, list, tableLayout);
            }
            if (viewModel.getCurrentTopSizeIndex().get() != -1) {
                lastSavedTopSize = viewModel.getCurrentTopSizeIndex().get();
                topSizeObserver.setCheckedSizeIndex(lastSavedTopSize);
                topSizeObserver.setState(lastSavedTopSize, true);
            }
        });
    }
}
