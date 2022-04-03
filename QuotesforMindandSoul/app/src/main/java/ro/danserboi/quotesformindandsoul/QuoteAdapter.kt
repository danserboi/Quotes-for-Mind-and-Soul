package ro.danserboi.quotesformindandsoul

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import android.content.Intent
import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ToggleButton
import java.util.ArrayList

/***
 * The adapter class for the RecyclerView, contains the quotes data.
 */
internal class QuoteAdapter(private val mContext: Context, private val mQuotesData: ArrayList<Quote?>?) : RecyclerView.Adapter<QuoteAdapter.ViewHolder>() {
    /**
     * Required method for creating the viewholder objects.
     *
     * @param parent   The ViewGroup into which the new View will be added
     * after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return The newly created ViewHolder.
     */
    override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.quote_list_item, parent, false))
    }

    /**
     * Required method that binds the data to the viewholder.
     *
     * @param holder   The viewholder into which the data should be put.
     * @param position The adapter position.
     */
    override fun onBindViewHolder(holder: ViewHolder,
                                  position: Int) {
        // Get current Quote.
        val currentQuote: Quote? = mQuotesData?.get(position)

        // Populate the textviews with data.
        if (currentQuote != null) {
            holder.bindTo(currentQuote)
        }
    }

    /**
     * Required method for determining the size of the data set.
     *
     * @return Size of the data set.
     */
    override fun getItemCount(): Int {
        return mQuotesData!!.size
    }

    /**
     * ViewHolder class that represents each row of data in the RecyclerView.
     */
    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        // Member Variables
        private val mWordsText: TextView = itemView.findViewById(R.id.words)
        private val mAuthorText: TextView = itemView.findViewById(R.id.author)
        private val mFavoriteButton: ToggleButton

        // The code inside the init block is the first to be executed when the class is instantiated.
        // The init block is run every time the class is instantiated.
        init {
            // Initialize the views.
            val mCopyButton: Button = itemView.findViewById(R.id.copyButton)
            mFavoriteButton = itemView.findViewById(R.id.favoriteButton)
            val mShareButton: Button = itemView.findViewById(R.id.shareButton)
            // Set OnClickListeners to buttons
            mCopyButton.setOnClickListener(CopyButtonOnClickListener(mContext, mWordsText, mAuthorText))
            mShareButton.setOnClickListener(ShareButtonOnClickListener(mContext, mWordsText, mAuthorText))
            // Set the OnClickListener to the entire view.
            itemView.setOnClickListener(this)
        }

        fun bindTo(currentQuote: Quote) {
            // Populate the textviews with data.
            mWordsText.text = currentQuote.words
            mAuthorText.text = currentQuote.author

            mFavoriteButton.setOnClickListener(BookmarkButtonOnClickListener(mContext, currentQuote))
            mFavoriteButton.isChecked = currentQuote.isBookmarked
        }

        override fun onClick(v: View) {
            val currentQuote: Quote? = mQuotesData!![bindingAdapterPosition]
            val detailIntent = Intent(mContext, SingleQuoteActivity::class.java)
            detailIntent.putExtra("position", bindingAdapterPosition)
            detailIntent.putExtra("quote", currentQuote)
            (mContext as Activity).startActivityForResult(detailIntent, Utils.FAVORITE_STATE_REQUEST)
        }
    }
}