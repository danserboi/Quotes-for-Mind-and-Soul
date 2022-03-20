package ro.danserboi.quotesformindandsoul;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ShareCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<String> {
    private ArrayList<Category> mCategoriesData;
    private CategoryAdapter mAdapter;
    private DrawerLayout drawer;
    private AnimationDrawable animationDrawable;

    public static final LinkedHashSet<Quote> recentQuotes = new LinkedHashSet<>();

    private QuoteRoomDatabase quoteRoomDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        quoteRoomDatabase = QuoteRoomDatabase.getDatabase(this);

        SharedPreferences mPreferences = getSharedPreferences(
                Utils.SHARED_PREF_FILE, MODE_PRIVATE);
        if (mPreferences.getString(Utils.DAILY_QUOTE_KEY, null) == null) {
            getQuoteOfTheDay();
        }

        RelativeLayout relativeLayout = findViewById(R.id.relative_layout);
        Drawable background = AppCompatResources.getDrawable(this, R.drawable.gradient_animation);
        relativeLayout.setBackground(background);
        animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(10);
        animationDrawable.setExitFadeDuration(2000);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            ViewGroup viewGroup = findViewById(android.R.id.content);
            final View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.search_quote_dialog, viewGroup, false);
            builder.setView(dialogView);
            builder.setPositiveButton(R.string.ok, (dialog, id) -> {
                // User clicked OK button
                RadioButton authorRadioButton = dialogView.findViewById(R.id.author_radio_button);
                RadioButton categoryRadioButton = dialogView.findViewById(R.id.category_radio_button);
                EditText inputTextView = dialogView.findViewById(R.id.input_text);
                String inputText = inputTextView.getText().toString();
                if (TextUtils.isEmpty(inputText)) {
                    Utils.displayToast(MainActivity.this, getString(R.string.text_not_entered));
                } else {
                    Intent searchIntent = new Intent(MainActivity.this, SearchQuotesActivity.class);
                    if (authorRadioButton.isChecked()) {
                        searchIntent.putExtra("method", NetworkUtils.QUERY_AUTHOR_PARAM);
                    } else if (categoryRadioButton.isChecked()) {
                        searchIntent.putExtra("method", NetworkUtils.QUERY_GENRE_PARAM);
                    }
                    searchIntent.putExtra("input", inputText);
                    startActivity(searchIntent);
                }
            });
            builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
                // User cancelled the dialog
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });


        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);
        // Initialize the RecyclerView.
        RecyclerView mRecyclerView = findViewById(R.id.categoryRecyclerView);
        // Set the GridLayoutManager.
        mRecyclerView.setLayoutManager(new
                GridLayoutManager(this, gridColumnCount));


        // Initialize the ArrayList that will contain the data.
        mCategoriesData = new ArrayList<>();
        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = new CategoryAdapter(this, mCategoriesData);
        mRecyclerView.setAdapter(mAdapter);
        initializeData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (animationDrawable != null && !animationDrawable.isRunning()) {
            animationDrawable.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }

    /**
     * Initialize the categories data from resources.
     */
    private void initializeData() {
        // Get the resources from the XML file.
        String[] categoriesList = getResources()
                .getStringArray(R.array.categories_titles);
        TypedArray categoriesImageResources =
                getResources().obtainTypedArray(R.array.categories_images);
        // Clear the existing data (to avoid duplication).
        mCategoriesData.clear();

        // Create the ArrayList of categories objects with titles for each category.
        for (int i = 0; i < categoriesList.length; i++) {
            mCategoriesData.add(new Category(categoriesList[i], categoriesImageResources.getResourceId(i, 0)));
        }
        categoriesImageResources.recycle();
        // Notify the adapter of the change.
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        switch (menuItem.getItemId()) {
            case R.id.nav_favorites:
                drawer.closeDrawers();
                startActivity(new Intent(this, FavoritesActivity.class));
                return true;
            case R.id.nav_quote_of_the_day:
                drawer.closeDrawers();
                startActivity(new Intent(this, QuoteOfTheDayActivity.class));
                return true;
            case R.id.nav_random_quote:
                drawer.closeDrawers();
                startActivity(new Intent(this, RandomQuoteActivity.class));
                return true;
            case R.id.nav_recent:
                drawer.closeDrawers();
                startActivity(new Intent(this, RecentQuotesActivity.class));
                return true;
            case R.id.nav_add_quote:
                drawer.closeDrawers();
                addQuote(findViewById(R.id.nav_add_quote));
                return true;
            case R.id.nav_settings:
                drawer.closeDrawers();
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.nav_share:
                drawer.closeDrawers();
                share();
                return true;
            case R.id.nav_rate:
                drawer.closeDrawers();
                rate();
                return true;
            case R.id.nav_feedback:
                drawer.closeDrawers();
                feedback();
                return true;
            case R.id.nav_about:
                drawer.closeDrawers();
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return false;
        }
    }

    public void addQuote(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        final View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.add_quote_dialog, viewGroup, false);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.dialog_add_button, (dialog, id) -> {
            // User clicked OK button
            EditText quoteInput = dialogView.findViewById(R.id.quote_input);
            final String quoteText = quoteInput.getText().toString();
            EditText authorInput = dialogView.findViewById(R.id.author_input);
            final String authorText = authorInput.getText().toString();
            if (TextUtils.isEmpty(quoteText) | TextUtils.isEmpty(authorText)) {
                Utils.displayToast(MainActivity.this, getString(R.string.empty_fields));
            } else {
                final List<Quote> queryQuotes = new ArrayList<>(1);
                Executors.newSingleThreadExecutor().execute(() -> {
                    QuoteRoomDatabase quoteRoomDatabase = QuoteRoomDatabase.getDatabase(MainActivity.this);
                    queryQuotes.addAll(quoteRoomDatabase.quoteDao().getQuote(quoteText));
                    if (queryQuotes.size() == 0) {
                        quoteRoomDatabase.quoteDao().insertQuote(new Quote(quoteText, authorText, null, true));
                    } else {
                        Quote alreadyInQuote = queryQuotes.get(0);
                        if (alreadyInQuote.getBookmarked()) {
                            Utils.displayToast(MainActivity.this, getString(R.string.quote_bookmarked));
                        } else {
                            Utils.displayToast(MainActivity.this, getString(R.string.added_to_favorites));
                        }
                    }
                });
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            // User cancelled the dialog
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void share() {
        String txt = getString(R.string.share_app_text) +
                "https://play.google.com/store/apps/details?id=" + getPackageName();
        String mimeType = "text/plain";
        new ShareCompat.IntentBuilder(this)
                .setType(mimeType)
                .setChooserTitle("Share this app with: ")
                .setText(txt)
                .setSubject(getString(R.string.share_app_subject))
                .startChooser();
    }

    public void rate() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e1) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            } catch (ActivityNotFoundException e2) {
                Utils.displayToast(this, getString(R.string.no_playstore));
            }
        }
    }

    public void feedback() {
        // Create and fire off our Intent in one fell swoop
        new ShareCompat.IntentBuilder(this)
                // chooser title
                .setChooserTitle(R.string.chooser_title_feedback)
                // most general text sharing MIME type
                .setType("text/plain")
                // feedback email address
                .setEmailTo(new String[]{getString(R.string.feedback_mail_address)})
                // mail subject
                .setSubject(getString(R.string.feedback_subject))
                /*
                 * The title of the chooser that the system will show
                 * to allow the user to select an app
                 */
                .startChooser();
    }

    public void getQuoteOfTheDay() {
        String queryString = "random";
        if (NetworkUtils.isConnected(this)) {
            Bundle queryBundle = new Bundle();
            queryBundle.putString("queryString", queryString);
            LoaderManager.getInstance(this).restartLoader(0, queryBundle, this);
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
            String words = quote.getString("quoteText");
            String author = quote.getString("quoteAuthor");
            String category = null;
            try {
                category = Utils.capitalizeWords(quote.getString("quoteGenre"));
            } catch (Exception ignored) {
            }
            // Insert quote into database
            final Quote quoteOfTheDay = new Quote(words, author, category, false);
            Executors.newSingleThreadExecutor().execute(() -> quoteRoomDatabase.quoteDao().insertQuote(quoteOfTheDay));
            // Add quote to SharedPreferences
            SharedPreferences mPreferences = getApplicationContext().getSharedPreferences(
                    Utils.SHARED_PREF_FILE, MODE_PRIVATE);
            SharedPreferences.Editor preferencesEditor = mPreferences.edit();
            preferencesEditor.putString(Utils.DAILY_QUOTE_KEY, quoteOfTheDay.getWords());
            preferencesEditor.putString(Utils.DAILY_AUTHOR_KEY, quoteOfTheDay.getAuthor());
            preferencesEditor.putString(Utils.DAILY_CAT_KEY, quoteOfTheDay.getCategory());
            preferencesEditor.putBoolean(Utils.DAILY_BOOKMARK_KEY, quoteOfTheDay.getBookmarked());
            preferencesEditor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
    }
}