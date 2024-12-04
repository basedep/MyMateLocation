package vaid.project.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

private var sharedPreferences: SharedPreferences? = null


class SessionUtil(context: Context) {

    init {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun saveSessionPreferences(
        sessionId: String,
        userId: String,
        userName: String,
        userEmail: String
    ) {
        sharedPreferences?.apply {
            edit()
                .putString("sessionId", sessionId)
                .putString("userId", userId)
                .putString("userName", userName)
                .putString("userEmail", userEmail)
                .apply()
        }
    }

    fun getPreference(prefKey: String): String{
        return sharedPreferences?.getString(prefKey, "").toString()
    }

    fun clearSessionPreferences(){
        sharedPreferences?.apply {
            edit().clear().apply()
        }
    }
}
