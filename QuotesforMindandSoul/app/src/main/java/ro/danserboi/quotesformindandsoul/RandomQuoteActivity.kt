package ro.danserboi.quotesformindandsoul

import android.app.Dialog
import ro.danserboi.quotesformindandsoul.QuoteRoomDatabase.Companion.getDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import org.json.JSONObject
import java.lang.Exception
import java.util.ArrayList
import java.util.concurrent.Executors

class RandomQuoteActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<String> {
    private var mFavoriteButton: ToggleButton? = null
    private var currentQuote: Quote? = null
    private var mQuoteRoomDatabase: QuoteRoomDatabase? = null
    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_quote)

        mQuoteRoomDatabase = getDatabase(this)

        val mWordsTextView: TextView = findViewById(R.id.wordsRandom)
        val mAuthorTextView: TextView = findViewById(R.id.authorRandom)
        val mCopyButton: Button = findViewById(R.id.copyButtonRandom)
        mFavoriteButton = findViewById(R.id.favoriteButtonRandom)
        val mShareButton: Button = findViewById(R.id.shareButtonRandom)
        val mChangeFontButton: Button = findViewById(R.id.changeFontButtonRandom)
        val mChangeBackgroundButton: Button = findViewById(R.id.changeBackgroundButtonRandom)

        randomQuote

        mCopyButton.setOnClickListener(CopyButtonOnClickListener(this, mWordsTextView, mAuthorTextView))
        mShareButton.setOnClickListener(ShareButtonOnClickListener(this, mWordsTextView, mAuthorTextView))
        mChangeFontButton.setOnClickListener(ChangeFontButtonOnClickListener(this, mWordsTextView, mAuthorTextView))
        mChangeBackgroundButton.setOnClickListener(ChangeBackgroundOnClickListener(this))
    }

    // We set ProgressBar while loading
    private val randomQuote: Unit
        get() {
            val queryString = "random"
            if (NetworkUtils.isConnected(this)) {
                val queryBundle = Bundle()
                queryBundle.putString("queryString", queryString)
                LoaderManager.getInstance(this).restartLoader(0, queryBundle, this)
                // We set ProgressBar while loading
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setView(R.layout.progress)
                dialog = builder.create()
                dialog?.show()
            } else {
                Utils.displayMessageAndFinishActivity(getString(R.string.check_connection), this)
            }
        }

    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<String?> {
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
            val queryQuotes: MutableList<Quote> = ArrayList(1)
            Executors.newSingleThreadExecutor().execute { queryQuotes.addAll(mQuoteRoomDatabase!!.quoteDao()!!.getQuote(words)) }
            // The quote is already in the database
            if (queryQuotes.size == 1) {
                currentQuote = queryQuotes[0]
            } else {
                val author = quote.getString("quoteAuthor")
                var category: String? = null
                try {
                    category = Utils.capitalizeWords(quote.getString("quoteGenre"))
                } catch (ignored: Exception) {
                }
                // Insert quote into database
                currentQuote = Quote(words, author, category, false)
                Executors.newSingleThreadExecutor().execute { mQuoteRoomDatabase!!.quoteDao()!!.insertQuote(currentQuote!!) }
            }

            // Set words and author in TextViews
            val wordsTextView: TextView = findViewById(R.id.wordsRandom)
            wordsTextView.text = words
            val authorTextView: TextView = findViewById(R.id.authorRandom)
            authorTextView.text = currentQuote!!.author

            // Set bookmark state
            mFavoriteButton!!.isChecked = currentQuote!!.isBookmarked

            // Add BookmarkButtonOnClickListener after we got the quote
            mFavoriteButton!!.setOnClickListener(BookmarkButtonOnClickListener(this, currentQuote!!))

            // We add quote to recent list
            MainActivity.recentQuotes.add(currentQuote!!)
        } catch (e: Exception) {
            e.printStackTrace()
            // If onPostExecute does not receive a proper JSON string,
            // we display toast to show failed results.
            Utils.displayMessageAndFinishActivity(getString(R.string.failed_to_load), this)
        } finally {
            // We hide the ProgressBar
            dialog!!.dismiss()
        }
    }

    override fun onLoaderReset(loader: Loader<String>) {}
    fun repeat(view: View?) {
        Utils.buttonAnimation(view!!)
        randomQuote
    }
}