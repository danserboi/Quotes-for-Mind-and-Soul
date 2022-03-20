package ro.danserboi.quotesformindandsoul;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class SearchQuotesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    private RecyclerView mRecyclerView;
    private QuoteAdapter mAdapter;
    private ArrayList<Quote> mQuotesData;
    private QuoteRoomDatabase mQuoteRoomDatabase;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_quotes);

        // Initialize the RecyclerView.
        mRecyclerView = findViewById(R.id.search_quotes_recyclerview);
        // Set the GridLayoutManager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Initialize the ArrayList that will contain the data.
        mQuotesData = new ArrayList<>();
        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = new QuoteAdapter(this, mQuotesData);
        mRecyclerView.setAdapter(mAdapter);

        mQuoteRoomDatabase = QuoteRoomDatabase.getDatabase(this);

        searchQuotes();
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

    public void searchQuotes() {
        // Get the search string from the input field.
        String searchMethod = getIntent().getStringExtra("method");
        String input = getIntent().getStringExtra("input");
        String queryString = "/" + searchMethod + "/" + input;
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
        assert queryString != null;
        String[] query = queryString.split("/");
        return new QuoteLoader(this, new Pair<>(query[1], query[2]));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {
        try {
            // Convert the response into a JSON object.
            JSONObject jsonObject = new JSONObject(s);
            // Get the JSONArray of quote items.
            JSONArray itemsArray = jsonObject.getJSONArray("data");
            // Initialize iterator and results fields.
            int i = 0;
            // Look for results in the items array
            while (i < itemsArray.length()) {
                // Get the current item information.
                JSONObject quote = itemsArray.getJSONObject(i);
                // Try to get words and author from the current item,
                // catch if either field is empty and move on.
                try {
                    String words = quote.getString("quoteText");
                    final List<Quote> queryQuotes = new ArrayList<>(1);
                    final String finalWords = words;
                    Executors.newSingleThreadExecutor().execute(() -> queryQuotes.addAll(mQuoteRoomDatabase.quoteDao().getQuote(finalWords)));
                    // The quote is already in the database
                    if (queryQuotes.size() == 1) {
                        mQuotesData.add(queryQuotes.get(0));
                    } else {
                        String author = quote.getString("quoteAuthor");
                        String category = null;
                        try {
                            category = Utils.capitalizeWords(quote.getString("quoteGenre"));
                        } catch (Exception ignored) {
                        }
                        // Insert quote into database and add it to recycler view
                        final Quote currentQuote = new Quote(words, author, category, false);
                        Executors.newSingleThreadExecutor().execute(() -> mQuoteRoomDatabase.quoteDao().insertQuote(currentQuote));
                        mQuotesData.add(currentQuote);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Move to the next item.
                i++;
            }
            if (i <= 0) {
                Utils.displayMessageAndFinishActivity("There are no " + jsonObject.getString("message").toLowerCase() + ".", this);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            // If onPostExecute does not receive a proper JSON string,
            // we display toast to show failed results.
            Utils.displayMessageAndFinishActivity(getString(R.string.failed_to_load), this);
        } finally {
            // We hide the ProgressBar
            if(dialog != null)
                dialog.dismiss();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
    }
}