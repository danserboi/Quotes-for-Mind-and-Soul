package ro.danserboi.quotesformindandsoul;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

public class QuoteLoader extends AsyncTaskLoader<String> {
    private final Pair<String, String> mQuery;

    QuoteLoader(Context context, Pair<String, String> query) {
        super(context);
        mQuery = query;
    }

    @Nullable
    @Override
    public String loadInBackground() {
        return NetworkUtils.getQuotes(mQuery);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}