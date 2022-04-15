package ro.danserboi.quotesformindandsoul

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.app.ShareCompat

class ShareButtonOnClickListener(private val context: Context, private val mWordsTextView: TextView, private val mAuthorTextView: TextView) : View.OnClickListener {
    override fun onClick(view: View) {
        Utils.buttonAnimation(view)
        val txt: String = "\"" + mWordsTextView.text + "\" - " + mAuthorTextView.text
        val mimeType = "text/plain"
        ShareCompat.IntentBuilder(context)
                .setType(mimeType)
                .setChooserTitle(R.string.share_chooser_title)
                .setText(txt)
                .setSubject(context.getString(R.string.share_subject))
                .startChooser()
    }
}