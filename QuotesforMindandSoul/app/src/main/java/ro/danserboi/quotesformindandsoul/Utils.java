package ro.danserboi.quotesformindandsoul;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class Utils {
    // Different keys and constants
    static final String DAILY_QUOTE_KEY = "daily_quote";
    static final String DAILY_AUTHOR_KEY = "daily_author";
    static final String DAILY_CAT_KEY = "daily_cat";
    static final String DAILY_BOOKMARK_KEY = "daily_bookmark";

    static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    static final String SHARED_PREF_FILE = "ro.danserboi.quotesformindandsoul";

    static final String DARK_MODE_KEY = "dark_mode";

    static final int FAVORITE_STATE_REQUEST = 1;

    static final String EXTRA_POSITION =
            "ro.danserboi.quotesformindandsoul.extra.POSITION";
    static final String EXTRA_STATE =
            "ro.danserboi.quotesformindandsoul.extra.STATE";

    static void buttonAnimation(View view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.9f, 1.0f, 0.9f, 1.0f, Animation.RELATIVE_TO_SELF, 0.9f, Animation.RELATIVE_TO_SELF, 0.9f);
        scaleAnimation.setDuration(100);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);
        view.startAnimation(scaleAnimation);
    }

    static void displayToast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    static void displayMessageAndFinishActivity(String message, AppCompatActivity activity) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        activity.finish();
    }

    public static String capitalizeWords(String text) {
        StringBuilder sb = new StringBuilder();
        if (text.length() > 0) {
            sb.append(Character.toUpperCase(text.charAt(0)));
        }
        for (int i = 1; i < text.length(); i++) {
            String chPrev = String.valueOf(text.charAt(i - 1));
            String ch = String.valueOf(text.charAt(i));

            if (Objects.equals(chPrev, " ")) {
                sb.append(ch.toUpperCase());
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}
