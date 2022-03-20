package ro.danserboi.quotesformindandsoul;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.concurrent.Executors;

public class FavoritesActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private QuoteAdapter mAdapter;
    private ArrayList<Quote> mQuotesData;
    private QuoteRoomDatabase mQuoteRoomDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Initialize the RecyclerView.
        mRecyclerView = findViewById(R.id.favorites_quotes_recyclerview);
        // Set the GridLayoutManager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(FavoritesActivity.this));

        // Initialize the ArrayList that will contain the data.
        mQuotesData = new ArrayList<>();

        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = new QuoteAdapter(FavoritesActivity.this, mQuotesData);
        mRecyclerView.setAdapter(mAdapter);

        mQuoteRoomDatabase = QuoteRoomDatabase.getDatabase(FavoritesActivity.this);

        Executors.newSingleThreadExecutor().execute(() -> {
            mQuotesData.addAll(mQuoteRoomDatabase.quoteDao().getQuotesByBookmark(true));
            mAdapter.notifyDataSetChanged();
        });
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
                    ToggleButton favoriteButton = mRecyclerView.getLayoutManager()
                            .findViewByPosition(quotePosition)
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