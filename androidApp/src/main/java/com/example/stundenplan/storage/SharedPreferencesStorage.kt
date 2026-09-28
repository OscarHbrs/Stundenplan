package com.example.stundenplan.storage

import android.content.Context
import com.example.stundenplan.data.KeyValueStorage

private const val PREFS_NAME = "stundenplan_prefs"

class SharedPreferencesStorage(context: Context) : KeyValueStorage {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getString(key: String): String? = prefs.getString(key, null)

    override fun getInt(key: String): Int? =
        if (prefs.contains(key)) prefs.getInt(key, 0) else null

    override fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    override fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }
}
