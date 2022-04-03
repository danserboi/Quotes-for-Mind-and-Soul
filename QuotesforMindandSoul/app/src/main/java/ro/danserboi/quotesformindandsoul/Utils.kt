package ro.danserboi.quotesformindandsoul

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.animation.BounceInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.lang.StringBuilder
import java.util.*

object Utils {
    // Different keys and constants
    const val DAILY_QUOTE_KEY = "daily_quote"
    const val DAILY_AUTHOR_KEY = "daily_author"
    const val DAILY_CAT_KEY = "daily_cat"
    const val DAILY_BOOKMARK_KEY = "daily_bookmark"
    const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    const val SHARED_PREF_FILE = "ro.danserboi.quotesformindandsoul"
    const val DARK_MODE_KEY = "dark_mode"
    const val FAVORITE_STATE_REQUEST = 1
    const val EXTRA_POSITION = "ro.danserboi.quotesformindandsoul.extra.POSITION"
    const val EXTRA_STATE = "ro.danserboi.quotesformindandsoul.extra.STATE"

    @JvmStatic
    fun buttonAnimation(view: View) {
        val scaleAnimation = ScaleAnimation(0.9f, 1.0f, 0.9f, 1.0f, Animation.RELATIVE_TO_SELF, 0.9f, Animation.RELATIVE_TO_SELF, 0.9f)
        scaleAnimation.duration = 100
        val bounceInterpolator = BounceInterpolator()
        scaleAnimation.interpolator = bounceInterpolator
        view.startAnimation(scaleAnimation)
    }

    @JvmStatic
    fun displayToast(context: Context?, s: String?) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
    }

    @JvmStatic
    fun displayMessageAndFinishActivity(message: String?, activity: AppCompatActivity) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        activity.finish()
    }

    @JvmStatic
    fun capitalizeWords(text: String): String {
        val sb = StringBuilder()
        if (text.isNotEmpty()) {
            sb.append(Character.toUpperCase(text[0]))
        }
        for (i in 1 until text.length) {
            val chPrev: String = text[i - 1].toString()
            val ch: String = text[i].toString()
            if (chPrev == " ") {
                sb.append(ch.uppercase(Locale.getDefault()))
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }
}