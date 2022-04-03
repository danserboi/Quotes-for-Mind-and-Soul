package ro.danserboi.quotesformindandsoul

import android.content.Context
import androidx.loader.content.AsyncTaskLoader

class QuoteLoader internal constructor(context: Context?, private val mQuery: Pair<String?, String?>) : AsyncTaskLoader<String?>(context!!) {
    override fun loadInBackground(): String? {
        return NetworkUtils.getQuotes(mQuery)
    }

    override fun onStartLoading() {
        super.onStartLoading()
        forceLoad()
    }
}