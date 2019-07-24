package ru.fitsme.android.data.repositories.clothes.entity;

import android.support.annotation.Nullable;

import java.util.List;

import ru.fitsme.android.domain.entities.clothes.ClothesItem;

public class ClothesPage {
    private long count;
    @Nullable
    private int next;
    private int previous;
    private List<ClothesItem> items;

    public long getCount() {
        return count;
    }

    public int getNext() {
        return next;
    }

    public int getPrevious() {
        return previous;
    }

    public List<ClothesItem> getItems() {
        return items;
    }
}
