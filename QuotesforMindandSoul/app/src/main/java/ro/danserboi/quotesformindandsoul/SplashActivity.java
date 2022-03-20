package ro.danserboi.quotesformindandsoul;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int darkMode = getSharedPreferences(
                Utils.SHARED_PREF_FILE, MODE_PRIVATE)
                .getInt(Utils.DARK_MODE_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(darkMode);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}