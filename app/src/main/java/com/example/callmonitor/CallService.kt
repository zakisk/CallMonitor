package com.example.callmonitor

import android.telecom.Call
import android.telecom.CallScreeningService
import android.telephony.SmsManager
import android.widget.Toast


class CallService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {

        val myPrefs = getSharedPreferences("Zaki", MODE_PRIVATE)
        val bool = myPrefs.getBoolean("bool", false)

        val scheme = callDetails.handle.scheme

        val builder = CallResponse.Builder()
            .setDisallowCall(bool)
            .setRejectCall(bool)
            .build()

        if (bool && scheme.equals("tel")) {
            val number = callDetails.handle.schemeSpecificPart
            var msg = myPrefs.getString("MSG", "")
            msg += "\n\n[CALL MONITOR APP]"
            val manager = SmsManager.getDefault()
            manager.sendTextMessage(number, null, msg, null, null)
        }

        respondToCall(callDetails, builder)

    }

}