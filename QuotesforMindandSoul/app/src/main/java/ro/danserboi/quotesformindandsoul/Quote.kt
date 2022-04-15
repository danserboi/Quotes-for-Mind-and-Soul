package ro.danserboi.quotesformindandsoul

import android.os.Parcelable
import android.os.Parcel
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Data model for each element of the RecyclerView from the CategoryActivity
 */
@Entity(tableName = "quotes_table")
class Quote : Parcelable {
    @PrimaryKey
    @ColumnInfo
    var words: String
    @ColumnInfo
    var author: String?
    @ColumnInfo
    var category: String?
    @ColumnInfo
    var isBookmarked: Boolean

    constructor(words: String, author: String?, category: String?, isBookmarked: Boolean) {
        this.words = words
        this.author = author
        this.category = category
        this.isBookmarked = isBookmarked
    }

    private constructor(`in`: Parcel) {
        words = Objects.requireNonNull(`in`.readString()).toString()
        author = `in`.readString()
        category = `in`.readString()
        isBookmarked = `in`.readByte().toInt() != 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Quote?> = object : Parcelable.Creator<Quote?> {
            override fun createFromParcel(`in`: Parcel): Quote {
                return Quote(`in`)
            }

            override fun newArray(size: Int): Array<Quote?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Quote) return false
        return words == other.words &&
                author == other.author
    }

    override fun hashCode(): Int {
        return Objects.hash(words, author)
    }

    override fun toString(): String {
        return "Quote{" +
                "words='" + words + '\'' +
                ", author='" + author + '\'' +
                '}'
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(words)
        dest.writeString(author)
        dest.writeString(category)
        dest.writeByte((if (isBookmarked) 1 else 0).toByte())
    }

}