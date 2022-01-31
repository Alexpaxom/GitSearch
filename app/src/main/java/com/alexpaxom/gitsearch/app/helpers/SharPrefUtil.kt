package com.alexpaxom.gitsearch.app.helpers

import android.content.Context
import android.content.SharedPreferences
import com.alexpaxom.gitsearch.di.screen.ScreenScope
import javax.inject.Inject

@ScreenScope
class SharPrefUtil @Inject constructor(
    private val context: Context
) {

    private val sharPref = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

    fun addToPref(data:String, key: String) {
        val editor: SharedPreferences.Editor = sharPref.edit()
        editor.putString(data, key)
        editor.apply()
    }

    fun getFromPref(key: String, defaultValue: String = ""): String {
        return sharPref.getString(key, defaultValue) ?: defaultValue
    }

    companion object {
        private const val APP_PREFERENCES = "settings"
    }
}