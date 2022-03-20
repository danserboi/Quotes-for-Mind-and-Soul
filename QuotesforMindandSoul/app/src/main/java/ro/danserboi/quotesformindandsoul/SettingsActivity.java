package ro.danserboi.quotesformindandsoul;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class SettingsActivity extends AppCompatActivity {
    private static int darkModeState = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mPreferences = getSharedPreferences(
                Utils.SHARED_PREF_FILE, MODE_PRIVATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putInt(Utils.DARK_MODE_KEY, darkModeState);
        preferencesEditor.apply();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference

            .OnPreferenceChangeListener {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            final Preference darkModePreference = findPreference("dark_mode_pref");
            if (darkModePreference != null) {
                darkModePreference.setOnPreferenceChangeListener(this);
                ((ListPreference)darkModePreference).setNegativeButtonText(getString(R.string.dialog_cancel));
            }

            final SwitchPreferenceCompat notificationSwitchPreference = findPreference("notification_switch_pref");
            if (notificationSwitchPreference != null) {
                final WorkManager mWorkManager = WorkManager.getInstance();
                Constraints constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();
                final PeriodicWorkRequest workRequest =
                        new PeriodicWorkRequest.Builder(MyWorker.class, 1, TimeUnit.DAYS)
                                .setConstraints(constraints)
                                .build();
                notificationSwitchPreference.setOnPreferenceClickListener(preference -> {
                    if (notificationSwitchPreference.isChecked()) {
                        mWorkManager.enqueue(workRequest);
                    } else {
                        mWorkManager.cancelWorkById(workRequest.getId());
                    }
                    return true;
                });
            }
        }

        @Override
        public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
            switch ((String) newValue) {
                case "Off":
                    AppCompatDelegate.setDefaultNightMode
                            (AppCompatDelegate.MODE_NIGHT_NO);
                    darkModeState = AppCompatDelegate.MODE_NIGHT_NO;
                    break;
                case "On":
                    AppCompatDelegate.setDefaultNightMode
                            (AppCompatDelegate.MODE_NIGHT_YES);
                    darkModeState = AppCompatDelegate.MODE_NIGHT_YES;
                    break;
                case "Follow system":
                default:
                    AppCompatDelegate.setDefaultNightMode
                            (AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    darkModeState = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                    break;
            }
            return true;
        }
    }
}