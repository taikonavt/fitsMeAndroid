package ru.fitsme.android.data.repositories.returns.entity;

import java.util.List;

import ru.fitsme.android.domain.entities.returns.ReturnsOrder;

public class ReturnsPage {
    private int count;
    private int current;
    private Integer next;
    private Integer previous;
    private List<ReturnsOrder> items;

    public int getCount() {
        return count;
    }

    public int getCurrent() {
        return current;
    }

    public Integer getNext() {
        return next;
    }

    public Integer getPrevious() {
        return previous;
    }

    public List<ReturnsOrder> getItems() {
        return items;
    }
}
