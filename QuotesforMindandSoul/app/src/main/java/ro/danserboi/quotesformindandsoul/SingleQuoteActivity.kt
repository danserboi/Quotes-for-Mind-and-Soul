package ro.danserboi.quotesformindandsoul

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.ImageButton
import android.content.Intent
import android.widget.Button
import android.widget.ToggleButton

class SingleQuoteActivity : AppCompatActivity() {
    private var mFavoriteButton: ToggleButton? = null
    private var quotePosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_quote)

        val mWordsTextView: TextView = findViewById(R.id.wordsSingle)
        val mAuthorTextView: TextView = findViewById(R.id.authorSingle)
        val mCopyButton: Button = findViewById(R.id.copyButtonSingle)
        mFavoriteButton = findViewById(R.id.favoriteButtonSingle)
        val mShareButton: Button = findViewById(R.id.shareButtonSingle)
        val mChangeFontButton: Button = findViewById(R.id.changeFontButtonSingle)
        val mChangeBackgroundButton: ImageButton = findViewById(R.id.changeBackgroundButtonSingle)

        val detailIntent: Intent = intent
        quotePosition = detailIntent.getIntExtra("position", -1)
        val currentQuote: Quote = detailIntent.getParcelableExtra("quote")!!
        val words: String = currentQuote.words
        val author: String? = currentQuote.author
        val isBookmarked: Boolean = currentQuote.isBookmarked
        // We add quote to recent list
        MainActivity.recentQuotes.add(currentQuote)
        // We set TextView values and Favorite Button checked state from intent
        mWordsTextView.text = words
        mAuthorTextView.text = author
        mFavoriteButton?.isChecked = isBookmarked
        // We set OnClickListeners to buttons
        mCopyButton.setOnClickListener(CopyButtonOnClickListener(this, mWordsTextView, mAuthorTextView))
        mFavoriteButton?.setOnClickListener(BookmarkButtonOnClickListener(this, currentQuote))
        mShareButton.setOnClickListener(ShareButtonOnClickListener(this, mWordsTextView, mAuthorTextView))
        mChangeFontButton.setOnClickListener(ChangeFontButtonOnClickListener(this, mWordsTextView, mAuthorTextView))
        mChangeBackgroundButton.setOnClickListener(ChangeBackgroundOnClickListener(this))
    }

    override fun onBackPressed() {
        val state: Boolean = mFavoriteButton!!.isChecked
        val stateIntent = Intent()
        stateIntent.putExtra(Utils.EXTRA_STATE, state)
        stateIntent.putExtra(Utils.EXTRA_POSITION, quotePosition)
        setResult(RESULT_OK, stateIntent)
        finish()
        super.onBackPressed()
    }
}