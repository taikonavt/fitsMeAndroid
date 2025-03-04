package ru.fitsme.android.presentation.fragments.returns.processing.six;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import ru.fitsme.android.BR;
import ru.fitsme.android.R;
import ru.fitsme.android.databinding.ItemReturnItemSelectedBinding;
import ru.fitsme.android.domain.entities.returns.ReturnsOrderItem;

public class SelectedReturnOrderItemsAdapter extends RecyclerView.Adapter<SelectedReturnOrderItemsAdapter.ReturnSelectedOrderItemsViewHolder> {

    private VerifyDataReturnViewModel viewModel;
    private List<ReturnsOrderItem> items;

    SelectedReturnOrderItemsAdapter(VerifyDataReturnViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public ReturnSelectedOrderItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemReturnItemSelectedBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_return_item_selected, parent, false);
        return new ReturnSelectedOrderItemsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReturnSelectedOrderItemsViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    void setItems(List<ReturnsOrderItem> items) {
        List<ReturnsOrderItem> selectedItemsList = new ArrayList<>();
        for (ReturnsOrderItem item : items) {
            if (item.getOrderItem().getClothe().isCheckedForReturn()) {
                selectedItemsList.add(item);
            }
        }
        this.items = selectedItemsList;
    }

    private ReturnsOrderItem getItem(int position) {
        return items.get(position);
    }

    class ReturnSelectedOrderItemsViewHolder extends RecyclerView.ViewHolder {
        final public ViewDataBinding binding;

        ReturnSelectedOrderItemsViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(int position) {
            binding.setVariable(BR.clothesItem, getItem(position).getOrderItem().getClothe());
            binding.setVariable(BR.viewModel, viewModel);
            binding.setVariable(BR.position, position);
            binding.executePendingBindings();
        }
    }
}
