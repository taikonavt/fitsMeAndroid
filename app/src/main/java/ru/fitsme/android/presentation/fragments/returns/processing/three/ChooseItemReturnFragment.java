package ru.fitsme.android.presentation.fragments.returns.processing.three;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import ru.fitsme.android.R;
import ru.fitsme.android.databinding.FragmentReturnChooseItemBinding;
import ru.fitsme.android.domain.entities.order.Order;
import ru.fitsme.android.domain.interactors.returns.IReturnsInteractor;
import ru.fitsme.android.presentation.common.listener.BackClickListener;
import ru.fitsme.android.presentation.fragments.base.BaseFragment;
import ru.fitsme.android.presentation.fragments.base.ViewModelFactory;
import timber.log.Timber;

public class ChooseItemReturnFragment extends BaseFragment<ChooseItemReturnViewModel> implements ChooseItemReturnBindingEvents, BackClickListener {

    @Inject
    IReturnsInteractor returnsInteractor;

    private static final String KEY_RETURNS_ORDER = "RETURNS_ORDER";

    private FragmentReturnChooseItemBinding binding;
    private ReturnOrderItemsAdapter adapter;
    private Order returnsOrder;

    public static ChooseItemReturnFragment newInstance(Order returnsOrder) {
        Bundle args = new Bundle();
        args.putParcelable(KEY_RETURNS_ORDER, returnsOrder);
        ChooseItemReturnFragment fragment = new ChooseItemReturnFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_return_choose_item, container, false);
        binding.setBindingEvents(this);
        binding.appBar.setBackClickListener(this);
        binding.appBar.setTitle(getResources().getString(R.string.returns_choose_items_header));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            returnsOrder = getArguments().getParcelable(KEY_RETURNS_ORDER);
            binding.setReturnsOrder(returnsOrder);
        }

        viewModel = ViewModelProviders.of(this,
                new ViewModelFactory(returnsInteractor)).get(ChooseItemReturnViewModel.class);
        if (savedInstanceState == null) {
            viewModel.init();
        }
        binding.setViewModel(viewModel);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        adapter = new ReturnOrderItemsAdapter(viewModel);

        binding.returnOrderItemsListRv.setLayoutManager(linearLayoutManager);
        binding.returnOrderItemsListRv.setHasFixedSize(true);
        binding.returnOrderItemsListRv.setAdapter(adapter);

        adapter.setItems(returnsOrder.getOrderItemList());

        viewModel.getErrorMsgLiveData().observeForever(this::onErrorMsg);
    }

    private void onErrorMsg(String msg) {
        if (msg.isEmpty()) return;
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void goBack() {
        viewModel.backToReturnsChooseOrder();
    }

    @Override
    public void onNext() {
        if (adapter.noItemsSelected()) {
            Timber.e("Ни одного элемента не выбрано");
            onErrorMsg("Ни одного элемента не выбрано");
        } else {
            viewModel.goToReturnsIndicateNumber(returnsOrder);
        }
    }

    @Override
    public void onBackPressed() {
        viewModel.onBackPressed();
    }
}
