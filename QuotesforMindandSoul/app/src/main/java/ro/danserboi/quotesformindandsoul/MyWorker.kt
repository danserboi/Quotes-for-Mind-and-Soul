package ro.danserboi.quotesformindandsoul

import androidx.work.WorkerParameters
import android.content.Intent
import android.app.PendingIntent
import android.app.NotificationManager
import android.os.Build
import android.app.NotificationChannel
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import java.util.*

class MyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val allQuotes: List<Quote> = QuoteRoomDatabase.getDatabase(applicationContext)?.quoteDao()!!.allQuotes
        val random = Random()
        val randomQuoteIdx: Int = random.nextInt(allQuotes.size)
        val quoteOfTheDay: Quote = allQuotes[randomQuoteIdx]
        // Add quote to SharedPreferences
        val mPreferences: SharedPreferences = applicationContext.getSharedPreferences(
                Utils.SHARED_PREF_FILE, Context.MODE_PRIVATE)
        val preferencesEditor: SharedPreferences.Editor = mPreferences.edit()
        preferencesEditor.putString(Utils.DAILY_QUOTE_KEY, quoteOfTheDay.words)
        preferencesEditor.putString(Utils.DAILY_AUTHOR_KEY, quoteOfTheDay.author)
        preferencesEditor.putString(Utils.DAILY_CAT_KEY, quoteOfTheDay.category)
        preferencesEditor.putBoolean(Utils.DAILY_BOOKMARK_KEY, quoteOfTheDay.isBookmarked)
        preferencesEditor.apply()
        showNotification(quoteOfTheDay.author!!, quoteOfTheDay.words)
        return Result.success()
    }

    private fun showNotification(task: String, desc: String) {
        // We open the QuoteOfTheDayActivity when we click on notification
        val intent = Intent(applicationContext, QuoteOfTheDayActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val resultPendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext,
                0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        val manager: NotificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelName: String = applicationContext.getString(R.string.channel_name)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(Utils.PRIMARY_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.enableLights(true)
            channel.lightColor = Color.BLUE
            channel.enableVibration(true)
            channel.description = applicationContext.getString(R.string.notification_description)
            manager.createNotificationChannel(channel)
        }
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, Utils.PRIMARY_CHANNEL_ID)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(task)
                .setContentText(desc)
                .setSmallIcon(R.drawable.ic_quote)
                .setAutoCancel(true)
        // This is for older Android devices
        builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
        // We make the notification expandable
        builder.setStyle(NotificationCompat.BigTextStyle()
                .setBigContentTitle(task)
                .bigText(desc))
        manager.notify(1, builder.build())
    }
}