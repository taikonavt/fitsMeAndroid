package ru.fitsme.android.data.repositories.clothes.entity;

public class RepoClotheProductName {
    private int id;
    private String title;
    private String type;

    public RepoClotheProductName(int id, String title, String type) {
        this.id = id;
        this.title = title;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
