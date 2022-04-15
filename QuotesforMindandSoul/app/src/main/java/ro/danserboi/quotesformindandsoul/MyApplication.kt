package ro.danserboi.quotesformindandsoul

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager


class MyApplication : Application() {
    var firstTime = true;
    var connected = true;

    override fun onCreate() {
        super.onCreate()
        registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    private val connectivityReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if(!firstTime) {
                if(NetworkUtils.isConnected(context!!)) {
                    if(!connected) {
                        Utils.displayToast(context, "Back online. Now you can search for new quotes.")
                        connected = true;
                    }
                } else {
                    if(connected) {
                        Utils.displayToast(context, "Connection lost. You can view only the quotes already stored.")
                        connected = false;
                    }
                }
            } else {
                firstTime = false;
            }
        }
    }
}