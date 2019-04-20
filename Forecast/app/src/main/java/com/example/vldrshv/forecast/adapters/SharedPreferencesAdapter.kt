package com.example.vldrshv.forecast.adapters

import android.content.Context
import android.content.SharedPreferences

/**
 * SHARED PREFERENCES ADAPTER
 *
 * Used for easier accessing to Shared Preferences (location)
 * --   uses keys (STRING)  to put context info
 * --   return FLOAT, STRING values
 *      **  INT value for LOCATION.ID
 *      **  FLOAT value for LOCATION.LAT & LOCATION.LNG
 *      **  STING value for LOCATION.NAME (localized or not)
 *
 * todo #1 update constructor to set SP_NAME
 */
class SharedPreferencesAdapter(context: Context) {
    private var sp: SharedPreferences? = null
    private var SP_NAME: String = "forecast_s_p"

    init {
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    }

    fun putString(key: String, str: String?) : SharedPreferencesAdapter {
        with(sp!!.edit()) {
            putString(key, str ?: "")
            apply()
        }

        return this
    }

    fun putFloat(key: String, value: Float) : SharedPreferencesAdapter {
        with(sp!!.edit()) {
            putFloat(key, value)
            apply()
        }
        return this
    }

    fun putInt(key: String, value: Int) : SharedPreferencesAdapter {
        with(sp!!.edit()) {
            putInt(key, value)
            apply()
        }
        return this
    }

    fun getString(key: String) : String {
        return sp!!.getString(key, "") ?: ""
    }

    fun getFloat(key: String) : Float {
        return sp!!.getFloat(key, 0f)
    }

    fun getInt(key: String) : Int {
        return sp!!.getInt(key, 0)
    }
}