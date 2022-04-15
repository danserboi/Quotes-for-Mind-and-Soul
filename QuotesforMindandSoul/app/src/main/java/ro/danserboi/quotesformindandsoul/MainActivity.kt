package ro.danserboi.quotesformindandsoul

import ro.danserboi.quotesformindandsoul.QuoteRoomDatabase.Companion.getDatabase
import ro.danserboi.quotesformindandsoul.Utils.displayToast
import ro.danserboi.quotesformindandsoul.NetworkUtils.isConnected
import ro.danserboi.quotesformindandsoul.Utils.displayMessageAndFinishActivity
import ro.danserboi.quotesformindandsoul.Utils.capitalizeWords
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import android.widget.RelativeLayout
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.view.ViewGroup
import android.view.LayoutInflater
import android.content.DialogInterface
import android.widget.EditText
import android.text.TextUtils
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.core.app.ShareCompat.IntentBuilder
import android.content.ActivityNotFoundException
import android.net.Uri
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import org.json.JSONObject
import java.lang.Exception
import java.util.ArrayList
import java.util.LinkedHashSet
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<String> {
    private var mCategoriesData: ArrayList<Category>? = null
    private var mAdapter: CategoryAdapter? = null
    private var drawer: DrawerLayout? = null
    private var animationDrawable: AnimationDrawable? = null

    companion object {
        val recentQuotes = LinkedHashSet<Quote>()
    }

    private var quoteRoomDatabase: QuoteRoomDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val darkMode = getSharedPreferences(
                Utils.SHARED_PREF_FILE, MODE_PRIVATE)
                .getInt(Utils.DARK_MODE_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(darkMode)

        setContentView(R.layout.activity_main)

        quoteRoomDatabase = getDatabase(this)

        val mPreferences = getSharedPreferences(
                Utils.SHARED_PREF_FILE, MODE_PRIVATE)
        if (mPreferences.getString(Utils.DAILY_QUOTE_KEY, null) == null) {
            quoteOfTheDay
        }

        val relativeLayout = findViewById<RelativeLayout>(R.id.relative_layout)
        val background = AppCompatResources.getDrawable(this, R.drawable.gradient_animation)
        relativeLayout.background = background
        animationDrawable = relativeLayout.background as AnimationDrawable
        animationDrawable!!.setEnterFadeDuration(10)
        animationDrawable!!.setExitFadeDuration(2000)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout_main)
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)
        if (drawer != null) {
            drawer!!.addDrawerListener(toggle)
        }
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView?.setNavigationItemSelectedListener(this)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { view: View ->
            val builder = AlertDialog.Builder(this@MainActivity)
            val viewGroup = findViewById<ViewGroup>(android.R.id.content)
            val dialogView = LayoutInflater.from(view.context).inflate(R.layout.search_quote_dialog, viewGroup, false)
            builder.setView(dialogView)
            builder.setPositiveButton(R.string.ok) { _: DialogInterface?, _: Int ->
                // User clicked OK button
                val authorRadioButton = dialogView.findViewById<RadioButton>(R.id.author_radio_button)
                val categoryRadioButton = dialogView.findViewById<RadioButton>(R.id.category_radio_button)
                val inputTextView = dialogView.findViewById<EditText>(R.id.input_text)
                val inputText = inputTextView.text.toString()
                if (TextUtils.isEmpty(inputText)) {
                    displayToast(this@MainActivity, getString(R.string.text_not_entered))
                } else {
                    val searchIntent = Intent(this@MainActivity, SearchQuotesActivity::class.java)
                    if (authorRadioButton.isChecked) {
                        searchIntent.putExtra("method", NetworkUtils.QUERY_AUTHOR_PARAM)
                    } else if (categoryRadioButton.isChecked) {
                        searchIntent.putExtra("method", NetworkUtils.QUERY_GENRE_PARAM)
                    }
                    searchIntent.putExtra("input", inputText)
                    startActivity(searchIntent)
                }
            }
            builder.setNegativeButton(R.string.cancel) { _: DialogInterface?, _: Int -> }
            val alertDialog = builder.create()
            alertDialog.show()
        }

        val gridColumnCount = resources.getInteger(R.integer.grid_column_count)
        // Initialize the RecyclerView.
        val mRecyclerView = findViewById<RecyclerView>(R.id.categoryRecyclerView)
        // Set the GridLayoutManager.
        mRecyclerView.layoutManager = GridLayoutManager(this, gridColumnCount)

        // Initialize the ArrayList that will contain the data.
        mCategoriesData = ArrayList()
        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = CategoryAdapter(this, mCategoriesData!!)
        mRecyclerView.adapter = mAdapter
        initializeData()
    }

    override fun onResume() {
        super.onResume()
        if (animationDrawable != null && !animationDrawable!!.isRunning) {
            animationDrawable!!.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if (animationDrawable != null && animationDrawable!!.isRunning) {
            animationDrawable!!.stop()
        }
    }

    /**
     * Initialize the categories data from resources.
     */
    private fun initializeData() {
        // Get the resources from the XML file.
        val categoriesList = resources
                .getStringArray(R.array.categories_titles)
        val categoriesImageResources = resources.obtainTypedArray(R.array.categories_images)
        // Clear the existing data (to avoid duplication).
        mCategoriesData!!.clear()

        // Create the ArrayList of categories objects with titles for each category.
        for (i in categoriesList.indices) {
            mCategoriesData!!.add(Category(categoriesList[i], categoriesImageResources.getResourceId(i, 0)))
        }
        categoriesImageResources.recycle()
        // Notify the adapter of the change.
        mAdapter!!.notifyDataSetChanged()
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        return when (menuItem.itemId) {
            R.id.nav_favorites -> {
                drawer!!.closeDrawers()
                startActivity(Intent(this, FavoritesActivity::class.java))
                true
            }
            R.id.nav_quote_of_the_day -> {
                drawer!!.closeDrawers()
                startActivity(Intent(this, QuoteOfTheDayActivity::class.java))
                true
            }
            R.id.nav_random_quote -> {
                drawer!!.closeDrawers()
                startActivity(Intent(this, RandomQuoteActivity::class.java))
                true
            }
            R.id.nav_recent -> {
                drawer!!.closeDrawers()
                startActivity(Intent(this, RecentQuotesActivity::class.java))
                true
            }
            R.id.nav_add_quote -> {
                drawer!!.closeDrawers()
                addQuote(findViewById(R.id.nav_add_quote))
                true
            }
            R.id.nav_settings -> {
                drawer!!.closeDrawers()
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.nav_share -> {
                drawer!!.closeDrawers()
                share()
                true
            }
            R.id.nav_rate -> {
                drawer!!.closeDrawers()
                rate()
                true
            }
            R.id.nav_feedback -> {
                drawer!!.closeDrawers()
                feedback()
                true
            }
            R.id.nav_about -> {
                drawer!!.closeDrawers()
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            else -> false
        }
    }

    private fun addQuote(view: View) {
        val builder = AlertDialog.Builder(this@MainActivity)
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val dialogView = LayoutInflater.from(view.context).inflate(R.layout.add_quote_dialog, viewGroup, false)
        builder.setView(dialogView)
        builder.setPositiveButton(R.string.dialog_add_button) { _: DialogInterface?, _: Int ->
            // User clicked OK button
            val quoteInput = dialogView.findViewById<EditText>(R.id.quote_input)
            val quoteText = quoteInput.text.toString()
            val authorInput = dialogView.findViewById<EditText>(R.id.author_input)
            val authorText = authorInput.text.toString()
            if (TextUtils.isEmpty(quoteText) or TextUtils.isEmpty(authorText)) {
                displayToast(this@MainActivity, getString(R.string.empty_fields))
            } else {
                val queryQuotes: MutableList<Quote> = ArrayList(1)
                Executors.newSingleThreadExecutor().execute {
                    val quoteRoomDatabase = getDatabase(this@MainActivity)
                    queryQuotes.addAll(quoteRoomDatabase!!.quoteDao()!!.getQuote(quoteText))
                    if (queryQuotes.size == 0) {
                        quoteRoomDatabase.quoteDao()!!.insertQuote(Quote(quoteText, authorText, null, true))
                    } else {
                        val alreadyInQuote = queryQuotes[0]
                        if (alreadyInQuote.isBookmarked) {
                            displayToast(this@MainActivity, getString(R.string.quote_bookmarked))
                        } else {
                            displayToast(this@MainActivity, getString(R.string.added_to_favorites))
                        }
                    }
                }
            }
        }
        builder.setNegativeButton(R.string.cancel) { _: DialogInterface?, _: Int -> }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun share() {
        val txt = getString(R.string.share_app_text) +
                "https://play.google.com/store/apps/details?id=" + packageName
        val mimeType = "text/plain"
        IntentBuilder(this)
                .setType(mimeType)
                .setChooserTitle("Share this app with: ")
                .setText(txt)
                .setSubject(getString(R.string.share_app_subject))
                .startChooser()
    }

    private fun rate() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e1: ActivityNotFoundException) {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
            } catch (e2: ActivityNotFoundException) {
                displayToast(this, getString(R.string.no_playstore))
            }
        }
    }

    private fun feedback() {
        // Create and fire off our Intent in one fell swoop
        IntentBuilder(this) // chooser title
                .setChooserTitle(R.string.chooser_title_feedback) // most general text sharing MIME type
                .setType("text/plain") // feedback email address
                .setEmailTo(arrayOf(getString(R.string.feedback_mail_address))) // mail subject
                .setSubject(getString(R.string.feedback_subject)) /*
                 * The title of the chooser that the system will show
                 * to allow the user to select an app
                 */
                .startChooser()
    }

    private val quoteOfTheDay: Unit
        get() {
            val queryString = "random"
            if (isConnected(this)) {
                val queryBundle = Bundle()
                queryBundle.putString("queryString", queryString)
                LoaderManager.getInstance(this).restartLoader(0, queryBundle, this)
            } else {
                displayMessageAndFinishActivity(getString(R.string.check_connection), this)
            }
        }

    override fun onCreateLoader(i: Int, bundle: Bundle?): QuoteLoader {
        var queryString: String? = ""
        if (bundle != null) {
            queryString = bundle.getString("queryString")
        }
        return QuoteLoader(this, Pair(queryString, ""))
    }

    override fun onLoadFinished(loader: Loader<String>, s: String) {
        try {
            // Convert the response into a JSON object.
            val jsonObject = JSONObject(s)
            // Get the JSONObject containing quote.
            val quote = jsonObject.getJSONArray("data").getJSONObject(0)
            // Get words and author from quote object
            val words = quote.getString("quoteText")
            val author = quote.getString("quoteAuthor")
            var category: String? = null
            try {
                category = capitalizeWords(quote.getString("quoteGenre"))
            } catch (ignored: Exception) {
            }
            // Insert quote into database
            val quoteOfTheDay = Quote(words, author, category, false)
            Executors.newSingleThreadExecutor().execute { quoteRoomDatabase!!.quoteDao()!!.insertQuote(quoteOfTheDay) }
            // Add quote to SharedPreferences
            val mPreferences = applicationContext.getSharedPreferences(
                    Utils.SHARED_PREF_FILE, MODE_PRIVATE)
            val preferencesEditor = mPreferences.edit()
            preferencesEditor.putString(Utils.DAILY_QUOTE_KEY, quoteOfTheDay.words)
            preferencesEditor.putString(Utils.DAILY_AUTHOR_KEY, quoteOfTheDay.author)
            preferencesEditor.putString(Utils.DAILY_CAT_KEY, quoteOfTheDay.category)
            preferencesEditor.putBoolean(Utils.DAILY_BOOKMARK_KEY, quoteOfTheDay.isBookmarked)
            preferencesEditor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onLoaderReset(loader: Loader<String>) {}
}