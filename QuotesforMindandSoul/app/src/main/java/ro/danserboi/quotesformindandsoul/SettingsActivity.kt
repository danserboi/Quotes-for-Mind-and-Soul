package ro.danserboi.quotesformindandsoul

import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.work.WorkManager
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.work.Constraints
import java.util.concurrent.TimeUnit

class SettingsActivity : AppCompatActivity() {
    companion object {
        private var darkModeState = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }
    private var mPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        mPreferences = getSharedPreferences(
                Utils.SHARED_PREF_FILE, MODE_PRIVATE)
    }

    override fun onPause() {
        super.onPause()
        val preferencesEditor: SharedPreferences.Editor = mPreferences!!.edit()
        preferencesEditor.putInt(Utils.DARK_MODE_KEY, darkModeState)
        preferencesEditor.apply()
    }

    class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val darkModePreference: Preference? = findPreference("dark_mode_pref")
            if (darkModePreference != null) {
                darkModePreference.onPreferenceChangeListener = this
                (darkModePreference as ListPreference).negativeButtonText = getString(R.string.dialog_cancel)
            }
            val notificationSwitchPreference: SwitchPreferenceCompat? = findPreference("notification_switch_pref")
            if (notificationSwitchPreference != null) {
                val mWorkManager: WorkManager = WorkManager.getInstance()
                val constraints: Constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                val workRequest: PeriodicWorkRequest = PeriodicWorkRequest.Builder(MyWorker::class.java, 1, TimeUnit.DAYS)
                        .setConstraints(constraints)
                        .build()
                notificationSwitchPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    if (notificationSwitchPreference.isChecked) {
                        mWorkManager.enqueue(workRequest)
                    } else {
                        mWorkManager.cancelWorkById(workRequest.id)
                    }
                    true
                }
            }
        }

        override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
            when (newValue as String) {
                "Off" -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    darkModeState = AppCompatDelegate.MODE_NIGHT_NO
                }
                "On" -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    darkModeState = AppCompatDelegate.MODE_NIGHT_YES
                }
                "Follow system" -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    darkModeState = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                else -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    darkModeState = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
            }
            return true
        }
    }

}