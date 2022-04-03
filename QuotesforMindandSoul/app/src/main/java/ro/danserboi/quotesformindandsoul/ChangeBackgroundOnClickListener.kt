package ro.danserboi.quotesformindandsoul

import android.content.Context
import android.content.res.Configuration
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.Window
import androidx.appcompat.content.res.AppCompatResources
import android.view.WindowManager

class ChangeBackgroundOnClickListener(private val context: Context) : View.OnClickListener {
    private var backgroundIdx: Int = 0
    companion object {
        private const val GRADIENTS_NO: Int = 13
    }

    override fun onClick(view: View) {
        Utils.buttonAnimation(view)
        val gradients: TypedArray = if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES ||
                ((AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_UNSPECIFIED ||
                        AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        && (context.resources.configuration.uiMode
                        and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)) {
            // Night mode is set explicitly or app follows the system that is in night mode
            context.resources.obtainTypedArray(R.array.background_gradients_dark)
        } else {
            // Day mode is set explicitly or app follows the system that is in day mode
            context.resources.obtainTypedArray(R.array.background_gradients)
        }
        val window: Window = (context as AppCompatActivity).window
        val background: Drawable? = AppCompatResources.getDrawable(context, gradients.getResourceId(backgroundIdx, -1))
        gradients.recycle()
        window.setBackgroundDrawable(background)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = context.getResources().getColor(android.R.color.transparent)
        window.navigationBarColor = context.getResources().getColor(android.R.color.transparent)
        backgroundIdx++
        if (backgroundIdx == GRADIENTS_NO) {
            backgroundIdx = 0
        }
    }

}