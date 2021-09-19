package com.example.callmonitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast

class CallBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_NEW_OUTGOING_CALL) {

            val num = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
            Log.d("Receiver", "Outgoing Call : $num")
        }
    }
}