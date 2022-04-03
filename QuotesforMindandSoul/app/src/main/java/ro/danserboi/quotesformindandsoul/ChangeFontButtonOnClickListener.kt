package ro.danserboi.quotesformindandsoul

import android.content.Context
import android.view.View
import android.widget.TextView

class ChangeFontButtonOnClickListener(private val context: Context, private val mWordsTextView: TextView, private val mAuthorTextView: TextView) : View.OnClickListener {
    private var fontIdx: Int = 0
    companion object {
        private const val FONTS_NO = 12
    }

    override fun onClick(view: View) {
        Utils.buttonAnimation(view)
        mWordsTextView.typeface = FontCache[context.resources.getStringArray(R.array.fonts)[fontIdx]]
        mAuthorTextView.typeface = FontCache[context.resources.getStringArray(R.array.fonts)[fontIdx]]
        fontIdx++
        if (fontIdx == FONTS_NO) {
            fontIdx = 0
        }
    }

}