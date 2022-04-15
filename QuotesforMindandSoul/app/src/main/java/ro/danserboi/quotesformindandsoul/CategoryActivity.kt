package ro.danserboi.quotesformindandsoul

import ro.danserboi.quotesformindandsoul.QuoteRoomDatabase.Companion.getDatabase
import ro.danserboi.quotesformindandsoul.NetworkUtils.isConnected
import ro.danserboi.quotesformindandsoul.Utils.displayMessageAndFinishActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.bumptech.glide.Glide
import android.content.Intent
import android.app.Dialog
import android.content.SharedPreferences
import android.content.res.TypedArray
import android.transition.TransitionInflater
import android.widget.ImageView
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import org.json.JSONObject
import java.lang.Exception
import java.lang.NullPointerException
import java.util.ArrayList
import java.util.concurrent.Executors

class CategoryActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<String> {
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: QuoteAdapter? = null
    private var mQuotesData: ArrayList<Quote?>? = null
    private var mQuoteRoomDatabase: QuoteRoomDatabase? = null
    private var currentQuotesCategory: String? = null
    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        // Initialize the RecyclerView.
        mRecyclerView = findViewById(R.id.quotes_recyclerview)
        // Set the GridLayoutManager.
        mRecyclerView?.layoutManager = LinearLayoutManager(this)
        // Initialize the ArrayList that will contain the data.
        mQuotesData = ArrayList()
        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = QuoteAdapter(this, mQuotesData)
        mRecyclerView?.adapter = mAdapter

        mQuoteRoomDatabase = getDatabase(this)

        currentQuotesCategory = intent.getStringExtra("title")
        val mPreferences: SharedPreferences = getSharedPreferences(Utils.SHARED_PREF_FILE, MODE_PRIVATE)
        val categoryAlreadyStored: Boolean = mPreferences.getBoolean(currentQuotesCategory, false)

        if (categoryAlreadyStored) {
            Executors.newSingleThreadExecutor().execute { mQuotesData!!.addAll(mQuoteRoomDatabase!!.quoteDao()!!.getQuotesByCategory(currentQuotesCategory!!)) }
            mAdapter!!.notifyDataSetChanged()
        } else {
            categoryQuotes
        }

        if (LoaderManager.getInstance(this).getLoader<Any>(0) != null) {
            LoaderManager.getInstance(this).initLoader(0, null, this)
        }

        val toolbar: Toolbar = findViewById(R.id.anim_toolbar)
        setSupportActionBar(toolbar)
        toolbar.navigationIcon = null
        if (supportActionBar != null) supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val collapsingToolbar: CollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        collapsingToolbar.title = currentQuotesCategory
        val categoryImage: ImageView = findViewById(R.id.categImageDetail)

        window.sharedElementEnterTransition = TransitionInflater.from(this).inflateTransition(R.transition.shared_element)

        val idx = intent.getIntExtra("image_resource", 0)
        val categoriesImageResources: TypedArray = resources.obtainTypedArray(R.array.categories_images_1440p)
        val imageResource: Int = categoriesImageResources.getResourceId(idx, 0)
        categoriesImageResources.recycle()
        Glide.with(this).load(imageResource)
                .into(categoryImage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Utils.FAVORITE_STATE_REQUEST) {
            if (resultCode == RESULT_OK) {
                assert(data != null)
                val favoriteState: Boolean = data!!.getBooleanExtra(Utils.EXTRA_STATE, false)
                val quotePosition: Int = data.getIntExtra(Utils.EXTRA_POSITION, -1)
                try {
                    val favoriteButton: ToggleButton? = mRecyclerView!!.layoutManager
                            ?.findViewByPosition(quotePosition)
                            ?.findViewById(R.id.favoriteButton)
                    // We also manually actualize favorite button state to make it appear faster
                    favoriteButton?.isChecked = favoriteState
                    mQuotesData!![quotePosition]!!.isBookmarked = favoriteState
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Get category String and create queryString
    private val categoryQuotes: Unit
        get() {
            // Get category String and create queryString
            val queryString = intent.getStringExtra("title")
            if (isConnected(this)) {
                val queryBundle = Bundle()
                queryBundle.putString("queryString", queryString)
                LoaderManager.getInstance(this).restartLoader(0, queryBundle, this)
                // We set ProgressBar while loading
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setView(R.layout.progress)
                dialog = builder.create()
                dialog?.show()
            } else {
                displayMessageAndFinishActivity(getString(R.string.check_connection), this)
            }
        }

    override fun onCreateLoader(i: Int, bundle: Bundle?): QuoteLoader {
        var queryString: String? = ""
        if (bundle != null) {
            queryString = bundle.getString("queryString")
        }
        return QuoteLoader(this, Pair(NetworkUtils.QUERY_GENRE_PARAM, queryString))
    }

    override fun onLoadFinished(loader: Loader<String>, s: String) {
        try {
            // Convert the response into a JSON object.
            val jsonObject = JSONObject(s)
            // Get the JSONArray of quote items.
            val itemsArray = jsonObject.getJSONArray("data")
            // Initialize iterator and results fields.
            var i = 0
            // Look for results in the items array
            while (i < itemsArray.length()) {
                // Get the current item information.
                val quote: JSONObject = itemsArray.getJSONObject(i)
                // Try to get words and author from the current item,
                // catch if either field is empty and move on.
                try {
                    val words = quote.getString("quoteText")
                    val queryQuotes: MutableList<Quote> = ArrayList(1)
                    Executors.newSingleThreadExecutor().execute { queryQuotes.addAll(mQuoteRoomDatabase!!.quoteDao()!!.getQuote(words)) }
                    // The quote is already in the database
                    if (queryQuotes.size == 1) {
                        mQuotesData!!.add(queryQuotes[0])
                    } else {
                        val author = quote.getString("quoteAuthor")
                        // Add quote to recycler view and database
                        val currentQuote = Quote(words, author, currentQuotesCategory, false)
                        mQuotesData!!.add(currentQuote)
                        Executors.newSingleThreadExecutor().execute { mQuoteRoomDatabase!!.quoteDao()!!.insertQuote(currentQuote) }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                // Move to the next item.
                i++
            }
            if (itemsArray.length() >= 100) {
                val mPreferences: SharedPreferences = getSharedPreferences(Utils.SHARED_PREF_FILE, MODE_PRIVATE)
                val preferencesEditor: SharedPreferences.Editor = mPreferences.edit()
                preferencesEditor.putBoolean(currentQuotesCategory, true)
                preferencesEditor.apply()
            }
            mAdapter!!.notifyDataSetChanged()
        } catch (e: Exception) {
            // If onPostExecute does not receive a proper JSON string,
            // we display toast to show failed results.
            displayMessageAndFinishActivity(getString(R.string.failed_to_load), this)
        } finally {
            // We hide the ProgressBar
            if (dialog != null) dialog!!.dismiss()
        }
    }

    override fun onLoaderReset(loader: Loader<String>) {}
}