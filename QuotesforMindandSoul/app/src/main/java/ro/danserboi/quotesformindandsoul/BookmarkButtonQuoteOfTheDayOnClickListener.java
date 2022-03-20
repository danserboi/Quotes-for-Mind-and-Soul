package ro.danserboi.quotesformindandsoul;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ToggleButton;

import java.util.concurrent.Executors;

public class BookmarkButtonQuoteOfTheDayOnClickListener implements View.OnClickListener {
    Context context;
    Quote quoteOfTheDay;

    public BookmarkButtonQuoteOfTheDayOnClickListener(Context context, Quote quoteOfTheDay) {
        this.context = context;
        this.quoteOfTheDay = quoteOfTheDay;
    }

    @Override
    public void onClick(View view) {
        Utils.buttonAnimation(view);
        if (((ToggleButton) view).isChecked()) {
            Utils.displayToast(context, context.getString(R.string.added_to_favorites));
            quoteOfTheDay.setBookmarked(true);

        } else {
            Utils.displayToast(context, context.getString(R.string.removed_from_favorites));
            quoteOfTheDay.setBookmarked(false);
        }
        final QuoteRoomDatabase mQuoteRoomDatabase = QuoteRoomDatabase.getDatabase(context);
        Executors.newSingleThreadExecutor().execute(() -> mQuoteRoomDatabase.quoteDao().updateQuote(quoteOfTheDay));
        SharedPreferences sharedPref = context.getSharedPreferences(Utils.SHARED_PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = sharedPref.edit();
        preferencesEditor.putBoolean(Utils.DAILY_BOOKMARK_KEY, quoteOfTheDay.getBookmarked());
        preferencesEditor.apply();
    }
}