package ro.danserboi.quotesformindandsoul

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import com.bumptech.glide.Glide
import android.content.Intent
import android.app.ActivityOptions
import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageView
import java.util.ArrayList

/***
 * The adapter class for the RecyclerView, contains the categories data.
 */
internal class CategoryAdapter(private val mContext: Context, private val mCategoriesData: ArrayList<Category>) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    /**
     * Required method for creating the viewholder objects.
     *
     * @param parent   The ViewGroup into which the new View will be added
     * after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return The newly created ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.category_list_item, parent, false))
    }

    /**
     * Required method that binds the data to the viewholder.
     *
     * @param holder   The viewholder into which the data should be put.
     * @param position The adapter position.
     */
    override fun onBindViewHolder(holder: ViewHolder,
                                  position: Int) {
        // Get current category.
        val currentCategory = mCategoriesData[position]

        // Populate text views with data.
        holder.bindTo(currentCategory)
    }

    /**
     * Required method for determining the size of the data set.
     *
     * @return Size of the data set.
     */
    override fun getItemCount(): Int {
        return mCategoriesData.size
    }

    /**
     * ViewHolder class that represents each row of data in the RecyclerView.
     */
    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        // Member Variables
        private val mTitleText: TextView = itemView.findViewById(R.id.categoryTitle)
        private val mCategoryImage: ImageView = itemView.findViewById(R.id.categoryImage)

        init {
            // Set the OnClickListener to the entire view.
            itemView.setOnClickListener(this)
        }

        fun bindTo(currentCategory: Category) {
            // Populate text views with data.
            mTitleText.text = currentCategory.title
            Glide.with(mContext).load(currentCategory.imageResource).into(mCategoryImage)
        }

        override fun onClick(v: View) {
            val currentCategory: Category = mCategoriesData[bindingAdapterPosition]
            val detailIntent = Intent(mContext, CategoryActivity::class.java)
            detailIntent.putExtra("title", currentCategory.title)
            detailIntent.putExtra("image_resource",
                    bindingAdapterPosition)
            val options: ActivityOptions = ActivityOptions
                    .makeSceneTransitionAnimation(mContext as Activity, mCategoryImage, "categoryImageSharedTransition")
            mContext.startActivity(detailIntent, options.toBundle())
        }
    }
}