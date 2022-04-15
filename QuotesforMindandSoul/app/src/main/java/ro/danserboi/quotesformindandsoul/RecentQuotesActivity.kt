package ro.danserboi.quotesformindandsoul

import ro.danserboi.quotesformindandsoul.QuoteRoomDatabase.Companion.getDatabase
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Intent
import android.widget.ToggleButton
import java.lang.NullPointerException
import java.util.*
import java.util.concurrent.Executors

class RecentQuotesActivity : AppCompatActivity() {
    private var mRecyclerView: RecyclerView? = null
    private var mQuotesData: ArrayList<Quote?>? = null
    private var mQuoteRoomDatabase: QuoteRoomDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_quotes)

        mQuoteRoomDatabase = getDatabase(this)

        // We actualize quotes bookmark state
        for (quote in MainActivity.recentQuotes) {
            val words = quote.words
            val queryQuotes: MutableList<Quote> = ArrayList(2)
            Executors.newSingleThreadExecutor().execute {
                queryQuotes.addAll(mQuoteRoomDatabase!!.quoteDao()!!.getQuote(words))
                // The quote is already in the database,
                // so we make sure that bookmark state is correct
                if (queryQuotes.size != 0) {
                    val currentQuote = queryQuotes[0]
                    quote.isBookmarked = currentQuote.isBookmarked
                }
            }
        }

        // Initialize the RecyclerView.
        mRecyclerView = findViewById(R.id.recent_quotes_recyclerview)
        // Set the GridLayoutManager.
        mRecyclerView?.layoutManager = LinearLayoutManager(this)
        // Initialize the ArrayList that will contain the data.
        mQuotesData = ArrayList()
        mQuotesData!!.addAll(MainActivity.recentQuotes)
        mQuotesData!!.reverse()
        // Initialize the adapter and set it to the RecyclerView.
        val mAdapter = QuoteAdapter(this, mQuotesData)
        mRecyclerView?.adapter = mAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Utils.FAVORITE_STATE_REQUEST) {
            if (resultCode == RESULT_OK) {
                assert(data != null)
                val favoriteState: Boolean = data!!.getBooleanExtra(Utils.EXTRA_STATE, false)
                val quotePosition: Int = data.getIntExtra(Utils.EXTRA_POSITION, -1)
                try {
                    val favoriteButton: ToggleButton? = Objects.requireNonNull(Objects.requireNonNull(mRecyclerView!!.layoutManager)
                            ?.findViewByPosition(quotePosition))
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
}