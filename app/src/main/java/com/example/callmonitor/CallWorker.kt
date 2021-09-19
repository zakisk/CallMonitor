package com.example.callmonitor

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.telecom.CallScreeningService
import android.util.Log
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters

class CallWorker(private val context: Context, workerParameters: WorkerParameters)
    : Worker(context, workerParameters) {
    override fun doWork(): Result {
        val myPrefs = context.getSharedPreferences("Zaki", MODE_PRIVATE)
        val editor = myPrefs.edit()
        editor.putBoolean("bool", false)
        editor.putString("MSG", "")
        editor.apply()
        return Result.success()
    }
}