package com.example.vldrshv.forecast

import android.content.Context
import android.content.SharedPreferences

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

    fun getString(key: String) : String {
        return sp!!.getString(key, "") ?: ""
    }

    fun getFloat(key: String) : Float {
        return sp!!.getFloat(key, 0f)
    }
}