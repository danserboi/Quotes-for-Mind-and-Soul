package ro.danserboi.quotesformindandsoul;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/***
 * The adapter class for the RecyclerView, contains the categories data.
 */
class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    // Member variables.
    private final ArrayList<Category> mCategoriesData;
    private final Context mContext;

    /**
     * Constructor that passes in the categories data and the context.
     *
     * @param categoriesData ArrayList containing the categories data.
     * @param context        Context of the application.
     */
    CategoryAdapter(Context context, ArrayList<Category> categoriesData) {
        this.mCategoriesData = categoriesData;
        this.mContext = context;
    }


    /**
     * Required method for creating the viewholder objects.
     *
     * @param parent   The ViewGroup into which the new View will be added
     *                 after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return The newly created ViewHolder.
     */
    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.category_list_item, parent, false));
    }

    /**
     * Required method that binds the data to the viewholder.
     *
     * @param holder   The viewholder into which the data should be put.
     * @param position The adapter position.
     */
    @Override
    public void onBindViewHolder(CategoryAdapter.ViewHolder holder,
                                 int position) {
        // Get current category.
        Category currentCategory = mCategoriesData.get(position);

        // Populate text views with data.
        holder.bindTo(currentCategory);
    }

    /**
     * Required method for determining the size of the data set.
     *
     * @return Size of the data set.
     */
    @Override
    public int getItemCount() {
        return mCategoriesData.size();
    }


    /**
     * ViewHolder class that represents each row of data in the RecyclerView.
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Member Variables
        private final TextView mTitleText;
        private final ImageView mCategoryImage;

        /**
         * Constructor for the ViewHolder, used in onCreateViewHolder().
         *
         * @param itemView The rootview of the category_list_item.xml layout file.
         */
        ViewHolder(View itemView) {
            super(itemView);

            // Initialize the views.
            mTitleText = itemView.findViewById(R.id.categoryTitle);
            mCategoryImage = itemView.findViewById(R.id.categoryImage);

            // Set the OnClickListener to the entire view.
            itemView.setOnClickListener(this);
        }

        void bindTo(Category currentCategory) {
            // Populate text views with data.
            mTitleText.setText(currentCategory.getTitle());
            Glide.with(mContext).load(currentCategory.getImageResource()).into(mCategoryImage);
        }

        @Override
        public void onClick(View v) {
            Category currentCategory = mCategoriesData.get(getBindingAdapterPosition());
            Intent detailIntent = new Intent(mContext, CategoryActivity.class);
            detailIntent.putExtra("title", currentCategory.getTitle());
            detailIntent.putExtra("image_resource",
                    getBindingAdapterPosition());
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation((Activity) mContext, mCategoryImage, "categoryImageSharedTransition");
            mContext.startActivity(detailIntent, options.toBundle());
        }
    }
}
