package ro.danserboi.quotesformindandsoul;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class ChangeFontButtonOnClickListener implements View.OnClickListener{
    private final Context context;
    private int fontIdx;
    private final TextView mWordsTextView;
    private final TextView mAuthorTextView;

    private final static int FONTS_NO = 12;

    public ChangeFontButtonOnClickListener(Context context, TextView mWordsTextView, TextView mAuthorTextView) {
        this.context = context;
        this.mWordsTextView = mWordsTextView;
        this.mAuthorTextView = mAuthorTextView;
    }

    @Override
    public void onClick(View view) {
        Utils.buttonAnimation(view);
        mWordsTextView.setTypeface(FontCache.get(context.getResources().getStringArray(R.array.fonts)[fontIdx]));
        mAuthorTextView.setTypeface(FontCache.get(context.getResources().getStringArray(R.array.fonts)[fontIdx]));
        fontIdx++;
        if(fontIdx == FONTS_NO) {
            fontIdx = 0;
        }
    }
}
