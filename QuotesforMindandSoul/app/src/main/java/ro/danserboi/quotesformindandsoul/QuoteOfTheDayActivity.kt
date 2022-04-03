package ro.danserboi.quotesformindandsoul

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.ImageButton
import android.widget.ToggleButton

class QuoteOfTheDayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_quote)

        val sharedPref: SharedPreferences = getSharedPreferences(Utils.SHARED_PREF_FILE, MODE_PRIVATE)
        val words = sharedPref.getString(Utils.DAILY_QUOTE_KEY, null)
        val author = sharedPref.getString(Utils.DAILY_AUTHOR_KEY, null)
        val category = sharedPref.getString(Utils.DAILY_CAT_KEY, null)
        val isBookmarked = sharedPref.getBoolean(Utils.DAILY_BOOKMARK_KEY, false)

        assert(words != null)
        val quoteOfTheDay = Quote(words!!, author, category, isBookmarked)

        val mWordsTextView: TextView = findViewById(R.id.wordsSingle)
        val mAuthorTextView: TextView = findViewById(R.id.authorSingle)
        val mCopyButton: Button = findViewById(R.id.copyButtonSingle)
        val mFavoriteButton: ToggleButton = findViewById(R.id.favoriteButtonSingle)
        val mShareButton: Button = findViewById(R.id.shareButtonSingle)
        val mChangeFontButton: Button = findViewById(R.id.changeFontButtonSingle)
        val mChangeBackgroundButton: ImageButton = findViewById(R.id.changeBackgroundButtonSingle)
        // We set TextView values and Favorite Button checked state from intent
        mWordsTextView.text = words
        mAuthorTextView.text = author
        mFavoriteButton.isChecked = isBookmarked
        // We set OnClickListeners to buttons
        mCopyButton.setOnClickListener(CopyButtonOnClickListener(this, mWordsTextView, mAuthorTextView))
        mFavoriteButton.setOnClickListener(BookmarkButtonQuoteOfTheDayOnClickListener(this, quoteOfTheDay))
        mShareButton.setOnClickListener(ShareButtonOnClickListener(this, mWordsTextView, mAuthorTextView))
        mChangeFontButton.setOnClickListener(ChangeFontButtonOnClickListener(this, mWordsTextView, mAuthorTextView))
        mChangeBackgroundButton.setOnClickListener(ChangeBackgroundOnClickListener(this))
        // We add it to recent quotes
        MainActivity.recentQuotes.add(quoteOfTheDay)
    }
}