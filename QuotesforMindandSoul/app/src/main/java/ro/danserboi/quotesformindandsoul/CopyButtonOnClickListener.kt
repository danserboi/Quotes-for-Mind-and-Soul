package ro.danserboi.quotesformindandsoul

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View

class CopyButtonOnClickListener(private val context: Context, private val mWordsTextView: TextView, private val mAuthorTextView: TextView) : View.OnClickListener {
    override fun onClick(view: View) {
        Utils.buttonAnimation(view)
        val txt: String = "\"" + mWordsTextView.text + "\" - " + mAuthorTextView.text
        val clipboard: ClipboardManager = (context as AppCompatActivity).getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("Quote", txt)
        clipboard.setPrimaryClip(clip)
        Utils.displayToast(context, context.getString(R.string.copied_to_clipboard))
    }
}