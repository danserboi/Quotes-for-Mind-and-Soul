package ro.danserboi.quotesformindandsoul;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

import static ro.danserboi.quotesformindandsoul.MainActivity.recentQuotes;

public class RecentQuotesActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ArrayList<Quote> mQuotesData;
    private QuoteRoomDatabase mQuoteRoomDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_quotes);

        mQuoteRoomDatabase = QuoteRoomDatabase.getDatabase(this);

        // We actualize quotes bookmark state
        for (final Quote quote : recentQuotes) {
            final String words = quote.getWords();
            final List<Quote> queryQuotes = new ArrayList<>(2);
            Executors.newSingleThreadExecutor().execute(() -> {
                queryQuotes.addAll(mQuoteRoomDatabase.quoteDao().getQuote(words));
                // The quote is already in the database,
                // so we make sure that bookmark state is correct
                if (queryQuotes.size() != 0) {
                    Quote currentQuote = queryQuotes.get(0);
                    quote.setBookmarked(currentQuote.getBookmarked());
                }
            });
        }

        // Initialize the RecyclerView.
        mRecyclerView = findViewById(R.id.recent_quotes_recyclerview);
        // Set the GridLayoutManager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Initialize the ArrayList that will contain the data.
        mQuotesData = new ArrayList<>();
        mQuotesData.addAll(recentQuotes);
        Collections.reverse(mQuotesData);
        // Initialize the adapter and set it to the RecyclerView.
        QuoteAdapter mAdapter = new QuoteAdapter(this, mQuotesData);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.FAVORITE_STATE_REQUEST) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                boolean favoriteState = data.getBooleanExtra(Utils.EXTRA_STATE, false);
                int quotePosition = data.getIntExtra(Utils.EXTRA_POSITION, -1);
                try {
                    ToggleButton favoriteButton = Objects.requireNonNull(Objects.requireNonNull(mRecyclerView.getLayoutManager())
                            .findViewByPosition(quotePosition))
                            .findViewById(R.id.favoriteButton);
                    // We also manually actualize favorite button state to make it appear faster
                    favoriteButton.setChecked(favoriteState);
                    mQuotesData.get(quotePosition).setBookmarked(favoriteState);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}