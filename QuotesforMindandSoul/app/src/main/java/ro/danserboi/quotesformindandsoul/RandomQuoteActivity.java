package ro.danserboi.quotesformindandsoul;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import static ro.danserboi.quotesformindandsoul.MainActivity.recentQuotes;

public class RandomQuoteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    private ToggleButton mFavoriteButton;
    private Quote currentQuote;
    private QuoteRoomDatabase mQuoteRoomDatabase;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_quote);

        mQuoteRoomDatabase = QuoteRoomDatabase.getDatabase(this);

        TextView mWordsTextView = findViewById(R.id.wordsRandom);
        TextView mAuthorTextView = findViewById(R.id.authorRandom);
        Button mCopyButton = findViewById(R.id.copyButtonRandom);
        mFavoriteButton = findViewById(R.id.favoriteButtonRandom);
        Button mShareButton = findViewById(R.id.shareButtonRandom);
        Button mChangeFontButton = findViewById(R.id.changeFontButtonRandom);
        Button mChangeBackgroundButton = findViewById(R.id.changeBackgroundButtonRandom);
        
        getRandomQuote();

        mCopyButton.setOnClickListener(new CopyButtonOnClickListener(this, mWordsTextView, mAuthorTextView));
        mShareButton.setOnClickListener(new ShareButtonOnClickListener(this, mWordsTextView, mAuthorTextView));
        mChangeFontButton.setOnClickListener(new ChangeFontButtonOnClickListener(this, mWordsTextView, mAuthorTextView));
        mChangeBackgroundButton.setOnClickListener(new ChangeBackgroundOnClickListener(this));
    }

    public void getRandomQuote() {
        String queryString = "random";
        if (NetworkUtils.isConnected(this)) {
            Bundle queryBundle = new Bundle();
            queryBundle.putString("queryString", queryString);
            LoaderManager.getInstance(this).restartLoader(0, queryBundle, this);
            // We set ProgressBar while loading
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(R.layout.progress);
            dialog = builder.create();
            dialog.show();
        } else {
            Utils.displayMessageAndFinishActivity(getString(R.string.check_connection), this);
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, @Nullable Bundle bundle) {
        String queryString = "";
        if (bundle != null) {
            queryString = bundle.getString("queryString");
        }
        return new QuoteLoader(this, new Pair<>(queryString, ""));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {
        try {
            // Convert the response into a JSON object.
            JSONObject jsonObject = new JSONObject(s);
            // Get the JSONObject containing quote.
            JSONObject quote = jsonObject.getJSONArray("data").getJSONObject(0);
            // Get words and author from quote object
            final String words = quote.getString("quoteText");
            final List<Quote> queryQuotes = new ArrayList<>(1);
            Executors.newSingleThreadExecutor().execute(() -> queryQuotes.addAll(mQuoteRoomDatabase.quoteDao().getQuote(words)));
            // The quote is already in the database
            if(queryQuotes.size() == 1) {
                currentQuote = queryQuotes.get(0);
            } else {
                String author = quote.getString("quoteAuthor");
                String category = null;
                try {
                    category = Utils.capitalizeWords(quote.getString("quoteGenre"));
                } catch (Exception ignored) {
                }
                // Insert quote into database
                currentQuote = new Quote(words, author, category, false);
                Executors.newSingleThreadExecutor().execute(() -> mQuoteRoomDatabase.quoteDao().insertQuote(currentQuote));
            }

            // Set words and author in TextViews
            TextView wordsTextView = findViewById(R.id.wordsRandom);
            wordsTextView.setText(words);

            TextView authorTextView = findViewById(R.id.authorRandom);
            authorTextView.setText(currentQuote.getAuthor());

            // Set bookmark state
            mFavoriteButton.setChecked(currentQuote.getBookmarked());

            // Add BookmarkButtonOnClickListener after we got the quote
            mFavoriteButton.setOnClickListener(new BookmarkButtonOnClickListener(this, currentQuote));

            // We add quote to recent list
            recentQuotes.add(currentQuote);
        } catch (Exception e) {
            e.printStackTrace();
            // If onPostExecute does not receive a proper JSON string,
            // we display toast to show failed results.
            Utils.displayMessageAndFinishActivity(getString(R.string.failed_to_load), this);
        } finally {
            // We hide the ProgressBar
            dialog.dismiss();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
    }

    public void repeat(View view) {
        Utils.buttonAnimation(view);
        getRandomQuote();
    }
}