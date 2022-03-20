package ro.danserboi.quotesformindandsoul;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.widget.ImageView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import android.transition.TransitionInflater;

import org.json.JSONArray;
import org.json.JSONObject;

public class CategoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    private RecyclerView mRecyclerView;
    private QuoteAdapter mAdapter;
    private ArrayList<Quote> mQuotesData;
    private QuoteRoomDatabase mQuoteRoomDatabase;
    private String currentQuotesCategory;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Initialize the RecyclerView.
        mRecyclerView = findViewById(R.id.quotes_recyclerview);
        // Set the GridLayoutManager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Initialize the ArrayList that will contain the data.
        mQuotesData = new ArrayList<>();
        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = new QuoteAdapter(this, mQuotesData);
        mRecyclerView.setAdapter(mAdapter);

        mQuoteRoomDatabase = QuoteRoomDatabase.getDatabase(this);

        currentQuotesCategory = getIntent().getStringExtra("title");
        SharedPreferences mPreferences = getSharedPreferences(Utils.SHARED_PREF_FILE, MODE_PRIVATE);
        boolean categoryAlreadyStored = mPreferences.getBoolean(currentQuotesCategory, false);

        if (categoryAlreadyStored) {
            Executors.newSingleThreadExecutor().execute(() -> mQuotesData.addAll(mQuoteRoomDatabase.quoteDao().getQuotesByCategory(currentQuotesCategory)));
            mAdapter.notifyDataSetChanged();
        } else {
            getCategoryQuotes();
        }

        if (LoaderManager.getInstance(this).getLoader(0) != null) {
            LoaderManager.getInstance(this).initLoader(0, null, this);
        }

        final Toolbar toolbar = findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(null);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(currentQuotesCategory);
        ImageView categoryImage = findViewById(R.id.categImageDetail);

        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_element));

        int idx = getIntent().getIntExtra("image_resource", 0);
        TypedArray categoriesImageResources =
                getResources().obtainTypedArray(R.array.categories_images_1440p);
        int imageResource = categoriesImageResources.getResourceId(idx, 0);
        categoriesImageResources.recycle();
        Glide.with(this).load(imageResource)
                .into(categoryImage);

    }

    /*
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Utils.BUNDLE_KEY, mQuotesData);
        outState.putInt(Utils.IMAGE_KEY, imageResource);
    }
    */

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

    public void getCategoryQuotes() {
        // Get category String and create queryString
        String queryString = getIntent().getStringExtra("title");
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
        return new QuoteLoader(this, new Pair<>(NetworkUtils.QUERY_GENRE_PARAM, queryString));
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
                        // Add quote to recycler view and database
                        final Quote currentQuote = new Quote(words, author, currentQuotesCategory, false);
                        mQuotesData.add(currentQuote);
                        Executors.newSingleThreadExecutor().execute(() -> mQuoteRoomDatabase.quoteDao().insertQuote(currentQuote));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Move to the next item.
                i++;
            }
            if(itemsArray.length() >= 100) {
                SharedPreferences mPreferences = getSharedPreferences(Utils.SHARED_PREF_FILE, MODE_PRIVATE);
                SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                preferencesEditor.putBoolean(currentQuotesCategory, true);
                preferencesEditor.apply();
            }
            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            // If onPostExecute does not receive a proper JSON string,
            // we display toast to show failed results.
            Utils.displayMessageAndFinishActivity(getString(R.string.failed_to_load), this);
        } finally {
            // We hide the ProgressBar
            if (dialog != null)
                dialog.dismiss();
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
    }
}