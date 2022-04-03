package ro.danserboi.quotesformindandsoul

import ro.danserboi.quotesformindandsoul.QuoteRoomDatabase.Companion.getDatabase
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Intent
import android.widget.ToggleButton
import java.lang.NullPointerException
import java.util.ArrayList
import java.util.concurrent.Executors

class FavoritesActivity : AppCompatActivity() {
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: QuoteAdapter? = null
    private var mQuotesData: ArrayList<Quote?>? = null
    private var mQuoteRoomDatabase: QuoteRoomDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        // Initialize the RecyclerView.
        mRecyclerView = findViewById(R.id.favorites_quotes_recyclerview)
        // Set the GridLayoutManager.
        mRecyclerView?.layoutManager = LinearLayoutManager(this@FavoritesActivity)

        // Initialize the ArrayList that will contain the data.
        mQuotesData = ArrayList()

        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = QuoteAdapter(this@FavoritesActivity, mQuotesData)
        mRecyclerView?.adapter = mAdapter
        mQuoteRoomDatabase = getDatabase(this@FavoritesActivity)
        Executors.newSingleThreadExecutor().execute {
            mQuotesData!!.addAll(mQuoteRoomDatabase!!.quoteDao()!!.getQuotesByBookmark(true))
            mAdapter!!.notifyDataSetChanged()
        }
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
}