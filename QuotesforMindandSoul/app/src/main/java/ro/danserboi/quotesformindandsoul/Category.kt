package ro.danserboi.quotesformindandsoul

import android.os.Parcelable
import android.os.Parcel

/**
 * Data model for each element of the RecyclerView from the MainActivity
 */
internal class Category : Parcelable {
    val imageResource: Int
    val title: String?

    constructor(title: String?, imageResource: Int) {
        this.title = title
        this.imageResource = imageResource
    }

    private constructor(`in`: Parcel) {
        title = `in`.readString()
        imageResource = `in`.readInt()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Category?> = object : Parcelable.Creator<Category?> {
            override fun createFromParcel(`in`: Parcel): Category {
                return Category(`in`)
            }

            override fun newArray(size: Int): Array<Category?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeInt(imageResource)
    }
}