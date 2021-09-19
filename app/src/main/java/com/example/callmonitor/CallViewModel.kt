package com.example.callmonitor

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException



class CallViewModel(preferences: SharedPreferences) : ViewModel() {

    val pref: MutableLiveData<Boolean> = MutableLiveData<Boolean>(preferences.getBoolean("bool", false))
}




@Suppress("UNCHECKED_CAST")
class CallViewModelFactory(private val preferences: SharedPreferences) :
            ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {


        if (modelClass.isAssignableFrom(CallViewModel::class.java)) {
            return CallViewModel(preferences = preferences) as T
        }

        throw IllegalArgumentException("Unknown View Model Class")
    }

}