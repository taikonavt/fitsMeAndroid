package ru.fitsme.android.domain.entities.clothes;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ClothesItem implements Parcelable {
    private int id;
    private String brand;
    private String name;
    private String description;
    private List<String> material;
    private String material_percentage;
    private List<Picture> pics;
    private List<Integer> available_sizes_id;
    private ClotheType clothe_type;
    private String size_in_stock;
    private int price;
    // TODO: change it:
    private boolean isCheckedForReturn;

    public ClothesItem() {
    }

    public ClothesItem(String brand, String name, int price) {
        this.brand = brand;
        this.name = name;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getMaterial() {
        return material;
    }

    public String getMaterialPercentage() {
        return material_percentage;
    }

    public List<Picture> getPics() {
        return pics;
    }

    public List<Integer> getAvailableSizesId() {
        return available_sizes_id;
    }

    public ClotheType getClotheType() {
        return clothe_type;
    }

    public SizeInStock getSizeInStock() {
        switch (size_in_stock) {
            case "UNDEFINED": {
                return SizeInStock.UNDEFINED;
            }
            case "NO": {
                return SizeInStock.NO;
            }
            case "YES": {
                return SizeInStock.YES;
            }
            default: {
                throw new IllegalStateException("Unknown ClotheItem SizeInStock state");
            }
        }
    }

    public int getPrice() {
        return price;
    }

    public boolean isCheckedForReturn() {
        return isCheckedForReturn;
    }

    public void setCheckedForReturn(boolean checkedForReturn) {
        isCheckedForReturn = checkedForReturn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClothesItem)) return false;
        ClothesItem that = (ClothesItem) o;
        return getId() == that.getId() &&
                getBrand().equals(that.getBrand()) &&
                getName().equals(that.getName()) &&
                getDescription().equals(that.getDescription()) &&
                getMaterial().equals(that.getMaterial()) &&
                getMaterialPercentage().equals(that.getMaterialPercentage()) &&
                getPics().equals(that.getPics()) &&
                getAvailableSizesId().equals(that.getAvailableSizesId()) &&
                getClotheType().equals(that.getClotheType()) &&
                getSizeInStock().equals(that.getSizeInStock()) &&
                getPrice() == that.getPrice() &&
                isCheckedForReturn() == that.isCheckedForReturn();
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + getId();
        result = 31 * result + getBrand().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getDescription().hashCode();
        result = 31 * result + getMaterial().hashCode();
        result = 31 * result + getMaterialPercentage().hashCode();
        result = 31 * result + getPics().hashCode();
        result = 31 * result + getAvailableSizesId().hashCode();
        result = 31 * result + getClotheType().hashCode();
        result = 31 * result + getSizeInStock().hashCode();
        result = 31 * result + getPrice();
        result = 31 * result + (isCheckedForReturn() ? 1231 : 1237);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(brand);
        out.writeString(name);
        out.writeString(description);
        out.writeList(material);
        out.writeString(material_percentage);
        out.writeList(pics);
        out.writeList(available_sizes_id);
        out.writeParcelable(clothe_type, flags);
        out.writeString(size_in_stock);
        out.writeInt(price);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            out.writeBoolean(isCheckedForReturn);
        } else {
            out.writeByte((byte) (isCheckedForReturn ? 1 : 0));
        }
    }

    public static final Parcelable.Creator<ClothesItem> CREATOR = new Parcelable.Creator<ClothesItem>() {
        public ClothesItem createFromParcel(Parcel in) {
            return new ClothesItem(in);
        }

        public ClothesItem[] newArray(int size) {
            return new ClothesItem[size];
        }
    };

    private ClothesItem(Parcel in) {
        id = in.readInt();
        brand = in.readString();
        name = in.readString();
        description = in.readString();
        in.readList(material, String.class.getClassLoader());
        material_percentage = in.readString();
        in.readList(pics, Picture.class.getClassLoader());
        in.readList(available_sizes_id, Integer.class.getClassLoader());
        clothe_type = in.readParcelable(ClotheType.class.getClassLoader());
        size_in_stock = in.readString();
        price = in.readInt();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isCheckedForReturn = in.readBoolean();
        } else {
            isCheckedForReturn = in.readByte() != 0;
        }
    }

    public enum SizeInStock {
        UNDEFINED, NO, YES
    }
}
