package ro.danserboi.quotesformindandsoul;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

/***
 * The adapter class for the RecyclerView, contains the quotes data.
 */
class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.ViewHolder> {

    // Member variables.
    private final ArrayList<Quote> mQuotesData;
    private final Context mContext;

    /**
     * Constructor that passes in the quotes data and the context.
     *
     * @param quotesData ArrayList containing the quotes data.
     * @param context    Context of the application.
     */
    QuoteAdapter(Context context, ArrayList<Quote> quotesData) {
        this.mQuotesData = quotesData;
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
    public QuoteAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.quote_list_item, parent, false));
    }

    /**
     * Required method that binds the data to the viewholder.
     *
     * @param holder   The viewholder into which the data should be put.
     * @param position The adapter position.
     */
    @Override
    public void onBindViewHolder(QuoteAdapter.ViewHolder holder,
                                 int position) {
        // Get current Quote.
        Quote currentQuote = mQuotesData.get(position);

        // Populate the textviews with data.
        holder.bindTo(currentQuote);
    }

    /**
     * Required method for determining the size of the data set.
     *
     * @return Size of the data set.
     */
    @Override
    public int getItemCount() {
        return mQuotesData.size();
    }


    /**
     * ViewHolder class that represents each row of data in the RecyclerView.
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Member Variables
        private final TextView mWordsText;
        private final TextView mAuthorText;
        private final ToggleButton mFavoriteButton;

        /**
         * Constructor for the ViewHolder, used in onCreateViewHolder().
         *
         * @param itemView The rootview of the Quote_list_item.xml layout file.
         */
        ViewHolder(View itemView) {
            super(itemView);

            // Initialize the views.
            mWordsText = itemView.findViewById(R.id.words);
            mAuthorText = itemView.findViewById(R.id.author);
            Button mCopyButton = itemView.findViewById(R.id.copyButton);
            mFavoriteButton = itemView.findViewById(R.id.favoriteButton);
            Button mShareButton = itemView.findViewById(R.id.shareButton);
            // Set OnClickListeners to buttons
            mCopyButton.setOnClickListener(new CopyButtonOnClickListener(mContext, mWordsText, mAuthorText));
            mShareButton.setOnClickListener(new ShareButtonOnClickListener(mContext, mWordsText, mAuthorText));
            // Set the OnClickListener to the entire view.
            itemView.setOnClickListener(this);
        }

        void bindTo(Quote currentQuote) {
            // Populate the textviews with data.
            mWordsText.setText(currentQuote.getWords());
            mAuthorText.setText(currentQuote.getAuthor());

            mFavoriteButton.setOnClickListener(new BookmarkButtonOnClickListener(mContext, currentQuote));
            mFavoriteButton.setChecked(currentQuote.getBookmarked());
        }

        @Override
        public void onClick(View v) {
            Quote currentQuote = mQuotesData.get(getBindingAdapterPosition());
            Intent detailIntent = new Intent(mContext, SingleQuoteActivity.class);
            detailIntent.putExtra("position", getBindingAdapterPosition());
            detailIntent.putExtra("quote", currentQuote);
            ((Activity) mContext).startActivityForResult(detailIntent, Utils.FAVORITE_STATE_REQUEST);
        }
    }
}