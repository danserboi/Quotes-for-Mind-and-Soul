package ro.danserboi.quotesformindandsoul

import android.graphics.Typeface
import java.lang.Exception
import java.util.*

object FontCache {
    private val fontCache = Hashtable<String, Typeface?>()
    operator fun get(name: String): Typeface? {
        var tf: Typeface? = fontCache[name]
        if (tf == null) {
            tf = try {
                Typeface.create(name, Typeface.NORMAL)
            } catch (e: Exception) {
                return null
            }
            fontCache[name] = tf
        }
        return tf
    }
}