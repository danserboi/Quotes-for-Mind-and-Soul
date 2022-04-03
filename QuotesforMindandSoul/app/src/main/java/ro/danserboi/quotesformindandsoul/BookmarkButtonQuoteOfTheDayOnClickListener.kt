package ro.danserboi.quotesformindandsoul

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.ToggleButton
import java.util.concurrent.Executors

class BookmarkButtonQuoteOfTheDayOnClickListener(var context: Context, private var quoteOfTheDay: Quote) : View.OnClickListener {
    override fun onClick(view: View) {
        Utils.buttonAnimation(view)
        if ((view as ToggleButton).isChecked) {
            Utils.displayToast(context, context.getString(R.string.added_to_favorites))
            quoteOfTheDay.isBookmarked = true
        } else {
            Utils.displayToast(context, context.getString(R.string.removed_from_favorites))
            quoteOfTheDay.isBookmarked = false
        }
        val mQuoteRoomDatabase: QuoteRoomDatabase? = QuoteRoomDatabase.getDatabase(context)
        Executors.newSingleThreadExecutor().execute { mQuoteRoomDatabase?.quoteDao()?.updateQuote(quoteOfTheDay) }
        val sharedPref: SharedPreferences = context.getSharedPreferences(Utils.SHARED_PREF_FILE, Context.MODE_PRIVATE)
        val preferencesEditor: SharedPreferences.Editor = sharedPref.edit()
        preferencesEditor.putBoolean(Utils.DAILY_BOOKMARK_KEY, quoteOfTheDay.isBookmarked)
        preferencesEditor.apply()
    }
}