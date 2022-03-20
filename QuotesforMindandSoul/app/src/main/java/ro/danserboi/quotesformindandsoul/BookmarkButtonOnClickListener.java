package ro.danserboi.quotesformindandsoul;

import android.content.Context;
import android.view.View;
import android.widget.ToggleButton;

import java.util.concurrent.Executors;

public class BookmarkButtonOnClickListener implements View.OnClickListener {
    Context context;
    Quote currentQuote;

    public BookmarkButtonOnClickListener(Context context, Quote currentQuote) {
        this.context = context;
        this.currentQuote = currentQuote;
    }

    @Override
    public void onClick(View view) {
        Utils.buttonAnimation(view);
        if (((ToggleButton) view).isChecked()) {
            Utils.displayToast(context, context.getString(R.string.added_to_favorites));
            currentQuote.setBookmarked(true);

        } else {
            Utils.displayToast(context, context.getString(R.string.removed_from_favorites));
            currentQuote.setBookmarked(false);
        }
        final QuoteRoomDatabase mQuoteRoomDatabase = QuoteRoomDatabase.getDatabase(context);
        Executors.newSingleThreadExecutor().execute(() -> mQuoteRoomDatabase.quoteDao().updateQuote(currentQuote));
    }

}
