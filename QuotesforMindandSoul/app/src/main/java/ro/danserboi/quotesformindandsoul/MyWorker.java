package ro.danserboi.quotesformindandsoul;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class MyWorker extends Worker {

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        List<Quote> allQuotes = QuoteRoomDatabase.getDatabase(getApplicationContext()).quoteDao().getAllQuotes();
        Random random = new Random();
        int randomQuoteIdx = random.nextInt(allQuotes.size());
        Quote quoteOfTheDay = allQuotes.get(randomQuoteIdx);
        // Add quote to SharedPreferences
        SharedPreferences mPreferences = getApplicationContext().getSharedPreferences(
                Utils.SHARED_PREF_FILE, MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putString(Utils.DAILY_QUOTE_KEY, quoteOfTheDay.getWords());
        preferencesEditor.putString(Utils.DAILY_AUTHOR_KEY, quoteOfTheDay.getAuthor());
        preferencesEditor.putString(Utils.DAILY_CAT_KEY, quoteOfTheDay.getCategory());
        preferencesEditor.putBoolean(Utils.DAILY_BOOKMARK_KEY, quoteOfTheDay.getBookmarked());
        preferencesEditor.apply();
        showNotification(quoteOfTheDay.getAuthor(), quoteOfTheDay.getWords());
        return Result.success();
    }


    private void showNotification(String task, String desc) {
        // We open the QuoteOfTheDayActivity when we click on notification
        Intent intent = new Intent(getApplicationContext() , QuoteOfTheDayActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelName = getApplicationContext().getString(R.string.channel_name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new
                    NotificationChannel(Utils.PRIMARY_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            channel.setDescription(getApplicationContext().getString(R.string.notification_description));
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), Utils.PRIMARY_CHANNEL_ID)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(task)
                .setContentText(desc)
                .setSmallIcon(R.drawable.ic_quote)
                .setAutoCancel(true);
        // This is for older Android devices
        builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        // We make the notification expandable
        builder.setStyle(new NotificationCompat.BigTextStyle()
                .setBigContentTitle(task)
                .bigText(desc));

        manager.notify(1, builder.build());
    }
}
