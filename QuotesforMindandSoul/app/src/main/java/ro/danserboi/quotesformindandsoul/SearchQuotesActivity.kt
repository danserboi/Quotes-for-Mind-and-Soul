package ro.danserboi.quotesformindandsoul

import ro.danserboi.quotesformindandsoul.QuoteRoomDatabase.Companion.getDatabase
import ro.danserboi.quotesformindandsoul.NetworkUtils.isConnected
import ro.danserboi.quotesformindandsoul.Utils.displayMessageAndFinishActivity
import ro.danserboi.quotesformindandsoul.Utils.capitalizeWords
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Intent
import android.app.Dialog
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import org.json.JSONObject
import java.lang.Exception
import java.lang.NullPointerException
import java.util.*
import java.util.concurrent.Executors

class SearchQuotesActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<String> {
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: QuoteAdapter? = null
    private var mQuotesData: ArrayList<Quote?>? = null
    private var mQuoteRoomDatabase: QuoteRoomDatabase? = null
    private var dialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_quotes)

        // Initialize the RecyclerView.
        mRecyclerView = findViewById(R.id.search_quotes_recyclerview)
        // Set the GridLayoutManager.
        mRecyclerView?.layoutManager = LinearLayoutManager(this)
        // Initialize the ArrayList that will contain the data.
        mQuotesData = ArrayList()
        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = QuoteAdapter(this, mQuotesData)
        mRecyclerView?.adapter = mAdapter
        mQuoteRoomDatabase = getDatabase(this)
        searchQuotes()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Utils.FAVORITE_STATE_REQUEST) {
            if (resultCode == RESULT_OK) {
                assert(data != null)
                val favoriteState = data!!.getBooleanExtra(Utils.EXTRA_STATE, false)
                val quotePosition = data.getIntExtra(Utils.EXTRA_POSITION, -1)
                try {
                    val favoriteButton = mRecyclerView!!.layoutManager
                            ?.findViewByPosition(quotePosition)
                            ?.findViewById<ToggleButton>(R.id.favoriteButton)
                    // We also manually actualize favorite button state to make it appear faster
                    favoriteButton?.isChecked = favoriteState
                    mQuotesData!![quotePosition]!!.isBookmarked = favoriteState
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun searchQuotes() {
        // Get the search string from the input field.
        val searchMethod = intent.getStringExtra("method")
        val input = intent.getStringExtra("input")
        val queryString = "/$searchMethod/$input"
        if (isConnected(this)) {
            val queryBundle = Bundle()
            queryBundle.putString("queryString", queryString)
            LoaderManager.getInstance(this).restartLoader(0, queryBundle, this)
            // We set ProgressBar while loading
            val builder = AlertDialog.Builder(this)
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
        assert(queryString != null)
        val query = queryString!!.split("/").toTypedArray()
        return QuoteLoader(this, Pair(query[1], query[2]))
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
                val quote = itemsArray.getJSONObject(i)
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
                        var category: String? = null
                        try {
                            category = capitalizeWords(quote.getString("quoteGenre"))
                        } catch (ignored: Exception) {
                        }
                        // Insert quote into database and add it to recycler view
                        val currentQuote = Quote(words, author, category, false)
                        Executors.newSingleThreadExecutor().execute { mQuoteRoomDatabase!!.quoteDao()!!.insertQuote(currentQuote) }
                        mQuotesData!!.add(currentQuote)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                // Move to the next item.
                i++
            }
            if (i <= 0) {
                displayMessageAndFinishActivity("There are no " + jsonObject.getString("message").lowercase(Locale.getDefault()) + ".", this)
            } else {
                mAdapter!!.notifyDataSetChanged()
            }
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