package ro.danserboi.quotesformindandsoul;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import static ro.danserboi.quotesformindandsoul.MainActivity.recentQuotes;

public class QuoteOfTheDayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_quote);

        SharedPreferences sharedPref = getSharedPreferences(Utils.SHARED_PREF_FILE, MODE_PRIVATE);
        String words = sharedPref.getString(Utils.DAILY_QUOTE_KEY, null);
        String author = sharedPref.getString(Utils.DAILY_AUTHOR_KEY, null);
        String category = sharedPref.getString(Utils.DAILY_CAT_KEY, null);
        boolean isBookmarked = sharedPref.getBoolean(Utils.DAILY_BOOKMARK_KEY, false);

        assert words != null;
        Quote quoteOfTheDay = new Quote(words, author, category, isBookmarked);

        TextView mWordsTextView = findViewById(R.id.wordsSingle);
        TextView mAuthorTextView = findViewById(R.id.authorSingle);
        Button mCopyButton = findViewById(R.id.copyButtonSingle);
        ToggleButton mFavoriteButton = findViewById(R.id.favoriteButtonSingle);
        Button mShareButton = findViewById(R.id.shareButtonSingle);
        Button mChangeFontButton = findViewById(R.id.changeFontButtonSingle);
        ImageButton mChangeBackgroundButton = findViewById(R.id.changeBackgroundButtonSingle);
        // We set TextView values and Favorite Button checked state from intent
        mWordsTextView.setText(words);
        mAuthorTextView.setText(author);
        mFavoriteButton.setChecked(isBookmarked);
        // We set OnClickListeners to buttons
        mCopyButton.setOnClickListener(new CopyButtonOnClickListener(this, mWordsTextView, mAuthorTextView));
        mFavoriteButton.setOnClickListener(new BookmarkButtonQuoteOfTheDayOnClickListener(this, quoteOfTheDay));
        mShareButton.setOnClickListener(new ShareButtonOnClickListener(this, mWordsTextView, mAuthorTextView));
        mChangeFontButton.setOnClickListener(new ChangeFontButtonOnClickListener(this, mWordsTextView, mAuthorTextView));
        mChangeBackgroundButton.setOnClickListener(new ChangeBackgroundOnClickListener(this));
        // We add it to recent quotes
        recentQuotes.add(quoteOfTheDay);
    }
}