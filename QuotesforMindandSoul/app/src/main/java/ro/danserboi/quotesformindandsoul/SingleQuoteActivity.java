package ro.danserboi.quotesformindandsoul;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import static ro.danserboi.quotesformindandsoul.MainActivity.recentQuotes;

public class SingleQuoteActivity extends AppCompatActivity {
    private ToggleButton mFavoriteButton;
    private Integer quotePosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_quote);

        TextView mWordsTextView = findViewById(R.id.wordsSingle);
        TextView mAuthorTextView = findViewById(R.id.authorSingle);
        Button mCopyButton = findViewById(R.id.copyButtonSingle);
        mFavoriteButton = findViewById(R.id.favoriteButtonSingle);
        Button mShareButton = findViewById(R.id.shareButtonSingle);
        Button mChangeFontButton = findViewById(R.id.changeFontButtonSingle);
        ImageButton mChangeBackgroundButton = findViewById(R.id.changeBackgroundButtonSingle);

        Intent detailIntent = getIntent();

        if(detailIntent != null) {
            quotePosition = detailIntent.getIntExtra("position", -1);
            Quote currentQuote = detailIntent.getParcelableExtra("quote");
            assert currentQuote != null;
            String words = currentQuote.getWords();
            String author = currentQuote.getAuthor();
            Boolean isBookmarked = currentQuote.getBookmarked();
            // We add quote to recent list
            recentQuotes.add(currentQuote);
            // We set TextView values and Favorite Button checked state from intent
            mWordsTextView.setText(words);
            mAuthorTextView.setText(author);
            mFavoriteButton.setChecked(isBookmarked);
            // We set OnClickListeners to buttons
            mCopyButton.setOnClickListener(new CopyButtonOnClickListener(this, mWordsTextView, mAuthorTextView));
            mFavoriteButton.setOnClickListener(new BookmarkButtonOnClickListener(this, currentQuote));
            mShareButton.setOnClickListener(new ShareButtonOnClickListener(this, mWordsTextView, mAuthorTextView));
            mChangeFontButton.setOnClickListener(new ChangeFontButtonOnClickListener(this, mWordsTextView, mAuthorTextView));
            mChangeBackgroundButton.setOnClickListener(new ChangeBackgroundOnClickListener(this));
        }
    }

    @Override
    public void onBackPressed() {
        Boolean state = mFavoriteButton.isChecked();
        Intent stateIntent = new Intent();
        stateIntent.putExtra(Utils.EXTRA_STATE, state);
        stateIntent.putExtra(Utils.EXTRA_POSITION, quotePosition);
        setResult(RESULT_OK, stateIntent);
        finish();
        super.onBackPressed();
    }
}