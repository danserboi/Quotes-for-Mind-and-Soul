package ro.danserboi.quotesformindandsoul;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

/**
 * Data model for each element of the RecyclerView from the CategoryActivity
 */
@Entity(tableName = "quotes_table")
public class Quote implements Parcelable {
    @PrimaryKey
    @ColumnInfo
    @NonNull
    private String words;
    @ColumnInfo
    private String author;
    @ColumnInfo
    private String category;
    @ColumnInfo
    private Boolean isBookmarked;

    public Quote(@NonNull String words, String author, String category, Boolean isBookmarked) {
        this.words = words;
        this.author = author;
        this.category = category;
        this.isBookmarked = isBookmarked;
    }

    private Quote(Parcel in) {
        words = Objects.requireNonNull(in.readString());
        author = in.readString();
        category = in.readString();
        isBookmarked = in.readByte() != 0;
    }

    public static final Creator<Quote> CREATOR = new Creator<Quote>() {
        @Override
        public Quote createFromParcel(Parcel in) {
            return new Quote(in);
        }

        @Override
        public Quote[] newArray(int size) {
            return new Quote[size];
        }
    };

    @NonNull
    public String getWords() {
        return words;
    }

    public void setWords(@NonNull String words) {
        this.words = words;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(Boolean bookmarked) {
        isBookmarked = bookmarked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quote)) return false;
        Quote quote = (Quote) o;
        return Objects.equals(getWords(), quote.getWords()) &&
                Objects.equals(getAuthor(), quote.getAuthor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWords(), getAuthor());
    }

    @NonNull
    @Override
    public String toString() {
        return "Quote{" +
                "words='" + words + '\'' +
                ", author='" + author + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(words);
        dest.writeString(author);
        dest.writeString(category);
        dest.writeByte((byte) (isBookmarked ? 1 : 0));
    }
}