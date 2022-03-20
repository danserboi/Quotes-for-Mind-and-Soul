package ro.danserboi.quotesformindandsoul;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Data model for each element of the RecyclerView from the MainActivity
 */
class Category implements Parcelable {
    private final int imageResource;
    private final String title;

    public Category(String title, int imageResource) {
        this.title = title;
        this.imageResource = imageResource;
    }

    private Category(Parcel in) {
        title = in.readString();
        imageResource = in.readInt();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public int getImageResource() {
        return imageResource;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeInt(imageResource);
    }
}
