package ro.danserboi.quotesformindandsoul

import android.content.Context
import android.view.View
import android.widget.ToggleButton
import java.util.concurrent.Executors

class BookmarkButtonOnClickListener(var context: Context, private var currentQuote: Quote) : View.OnClickListener {
    override fun onClick(view: View) {
        Utils.buttonAnimation(view)
        if ((view as ToggleButton).isChecked) {
            Utils.displayToast(context, context.getString(R.string.added_to_favorites))
            currentQuote.isBookmarked = true
        } else {
            Utils.displayToast(context, context.getString(R.string.removed_from_favorites))
            currentQuote.isBookmarked = false
        }
        val mQuoteRoomDatabase: QuoteRoomDatabase? = QuoteRoomDatabase.getDatabase(context)
        Executors.newSingleThreadExecutor().execute { mQuoteRoomDatabase?.quoteDao()?.updateQuote(currentQuote) }
    }
}