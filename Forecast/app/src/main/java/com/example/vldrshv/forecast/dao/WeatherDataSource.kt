package com.example.vldrshv.forecast.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.example.vldrshv.forecast.DailyForecast
import com.example.vldrshv.forecast.Weather

// TODO : API + DATA_BASE
class WeatherDataSource(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    object WeatherEntry : BaseColumns {
        const val TABLE_NAME = "weather"
        const val _ID = "id"
        const val COLUMN_CITY_ID = "city_id"
        const val COLUMN_DATE = "date"
        const val COLUMN_SUN_RISE_TIME = "sun_rise_time"
        const val COLUMN_SUN_SET_TIME = "sun_set_time"
        const val COLUMN_MOON_RISE_TIME = "moon_rise_time"
        const val COLUMN_MOON_SET_TIME = "moon_set_time"
        const val COLUMN_MIN_TEMPERATURE = "min_temperature"
        const val COLUMN_MIN_TEMPERATURE_UNIT = "min_temperature_unit"
        const val COLUMN_MAX_TEMPERATURE = "max_temperature"
        const val COLUMN_MAX_TEMPERATURE_UNIT = "max_temperature_unit"
        const val COLUMN_MIN_REAL_FEEL_TEMPERATURE = "min_real_feel_temp"
        const val COLUMN_MIN_REAL_FEEL_TEMPERATURE_UNIT = "min_real_feel_temp_unit"
        const val COLUMN_MAX_REAL_FEEL_TEMPERATURE = "max_real_feel_temp"
        const val COLUMN_MAX_REAL_FEEL_TEMPERATURE_UNIT = "max_real_feel_temp_unit"
        const val COLUMN_MIN_REAL_FEEL_TEMPERATURE_SHADE = "min_real_feel_temp_shade"
        const val COLUMN_MIN_REAL_FEEL_TEMPERATURE_SHADE_UNIT = "min_real_feel_temp_shade_unit"
        const val COLUMN_MAX_REAL_FEEL_TEMPERATURE_SHADE = "max_real_feel_temp_shade"
        const val COLUMN_MAX_REAL_FEEL_TEMPERATURE_SHADE_UNIT = "max_real_feel_temp_shade_unit"
        const val COLUMN_HOURS_OF_SUN = "hours_of_sun"
    }

    private val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${WeatherEntry.TABLE_NAME} (" +
                    "${WeatherEntry._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${WeatherEntry.COLUMN_CITY_ID} INTEGER," +
                    "${WeatherEntry.COLUMN_DATE} TEXT," +
                    "${WeatherEntry.COLUMN_SUN_RISE_TIME} TEXT," +
                    "${WeatherEntry.COLUMN_SUN_SET_TIME} TEXT," +
                    "${WeatherEntry.COLUMN_MOON_RISE_TIME} TEXT," +
                    "${WeatherEntry.COLUMN_MOON_SET_TIME} TEXT," +
                    "${WeatherEntry.COLUMN_MIN_TEMPERATURE} FLOAT," +
                    "${WeatherEntry.COLUMN_MIN_TEMPERATURE_UNIT} TEXT," +
                    "${WeatherEntry.COLUMN_MAX_TEMPERATURE} FLOAT," +
                    "${WeatherEntry.COLUMN_MAX_TEMPERATURE_UNIT} TEXT," +
                    "${WeatherEntry.COLUMN_MIN_REAL_FEEL_TEMPERATURE} FLOAT," +
                    "${WeatherEntry.COLUMN_MIN_REAL_FEEL_TEMPERATURE_UNIT} TEXT," +
                    "${WeatherEntry.COLUMN_MAX_REAL_FEEL_TEMPERATURE} FLOAT," +
                    "${WeatherEntry.COLUMN_MAX_REAL_FEEL_TEMPERATURE_UNIT} TEXT," +
                    "${WeatherEntry.COLUMN_MIN_REAL_FEEL_TEMPERATURE_SHADE} FLOAT," +
                    "${WeatherEntry.COLUMN_MIN_REAL_FEEL_TEMPERATURE_SHADE_UNIT} TEXT," +
                    "${WeatherEntry.COLUMN_MAX_REAL_FEEL_TEMPERATURE_SHADE} FLOAT," +
                    "${WeatherEntry.COLUMN_MAX_REAL_FEEL_TEMPERATURE_SHADE_UNIT} TEXT," +
                    "${WeatherEntry.COLUMN_HOURS_OF_SUN} FLOAT)"
    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${WeatherEntry.TABLE_NAME}"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "WeatherDataSource.db"
    }

    private fun insert(weather: Weather, cityId: Int) {
        val db = this.writableDatabase
        val value = ContentValues().apply {
            put(WeatherEntry.COLUMN_CITY_ID, cityId)
            put(WeatherEntry.COLUMN_DATE, weather.date)
            put(WeatherEntry.COLUMN_SUN_RISE_TIME, weather.sun.riseTime)
            put(WeatherEntry.COLUMN_SUN_SET_TIME, weather.sun.setTime)
            put(WeatherEntry.COLUMN_MOON_RISE_TIME, weather.moon.riseTime)
            put(WeatherEntry.COLUMN_MOON_SET_TIME, weather.moon.setTime)
            put(WeatherEntry.COLUMN_MIN_TEMPERATURE, weather.temperature.minimum.value)
            put(WeatherEntry.COLUMN_MIN_TEMPERATURE_UNIT, weather.temperature.minimum.unit)
            put(WeatherEntry.COLUMN_MAX_TEMPERATURE, weather.temperature.maximum.value)
            put(WeatherEntry.COLUMN_MAX_TEMPERATURE_UNIT, weather.temperature.maximum.unit)
            put(WeatherEntry.COLUMN_MIN_REAL_FEEL_TEMPERATURE, weather.realFeelTemperature.minimum.value)
            put(WeatherEntry.COLUMN_MIN_REAL_FEEL_TEMPERATURE_UNIT, weather.realFeelTemperature.minimum.unit)
            put(WeatherEntry.COLUMN_MAX_REAL_FEEL_TEMPERATURE, weather.realFeelTemperature.maximum.value)
            put(WeatherEntry.COLUMN_MAX_REAL_FEEL_TEMPERATURE_UNIT, weather.realFeelTemperature.maximum.unit)
            put(WeatherEntry.COLUMN_MIN_REAL_FEEL_TEMPERATURE_SHADE, weather.realFeelTemperatureShade.minimum.value)
            put(WeatherEntry.COLUMN_MIN_REAL_FEEL_TEMPERATURE_SHADE_UNIT, weather.realFeelTemperatureShade.minimum.unit)
            put(WeatherEntry.COLUMN_MAX_REAL_FEEL_TEMPERATURE_SHADE, weather.realFeelTemperatureShade.maximum.value)
            put(WeatherEntry.COLUMN_MAX_REAL_FEEL_TEMPERATURE_SHADE_UNIT, weather.realFeelTemperatureShade.maximum.unit)
            put(WeatherEntry.COLUMN_HOURS_OF_SUN, weather.hoursOfSun)
        }
        val list: List<Weather> = select(cityId)
        if (!list.isEmpty())
            db?.delete(WeatherEntry.TABLE_NAME, null, null)

        db?.insert(WeatherEntry.TABLE_NAME, null, value)
    }

    fun select(cityId: Int): List<Weather> {
        val db = this.writableDatabase
        val cursor = db.query(
                WeatherEntry.TABLE_NAME,              // The table to query
                null, //projection,           // The array of columns to return (pass null to get all)
                "${WeatherEntry.COLUMN_CITY_ID} = ?", //selection,            // The columns for the WHERE clause
                arrayOf(cityId.toString()), //selectionArgs,     // The values for the WHERE clause
                null,                          // don't group the rows
                null,                           // don't filter by row groups
                null //sortOrder               // The sort order
        )
        val items = arrayListOf<Weather>()
        with(cursor){
            while (moveToNext()) {
                val weather = Weather()
                weather.date = getString(getColumnIndexOrThrow(WeatherEntry.COLUMN_DATE))
                weather.sun.riseTime = getString(getColumnIndexOrThrow(WeatherEntry.COLUMN_SUN_RISE_TIME))
                weather.sun.setTime = getString(getColumnIndexOrThrow(WeatherEntry.COLUMN_SUN_SET_TIME))
                weather.moon.riseTime = getString(getColumnIndexOrThrow(WeatherEntry.COLUMN_MOON_RISE_TIME)) ?: ""
                weather.moon.setTime = getString(getColumnIndexOrThrow(WeatherEntry.COLUMN_MOON_SET_TIME)) ?: ""
                weather.temperature.minimum.value = getFloat(getColumnIndexOrThrow(WeatherEntry.COLUMN_MIN_TEMPERATURE))
                weather.temperature.minimum.unit = getString(getColumnIndexOrThrow(WeatherEntry.COLUMN_MIN_TEMPERATURE_UNIT))
                weather.temperature.maximum.value = getFloat(getColumnIndexOrThrow(WeatherEntry.COLUMN_MAX_TEMPERATURE))
                weather.temperature.maximum.unit = getString(getColumnIndexOrThrow(WeatherEntry.COLUMN_MAX_TEMPERATURE_UNIT))
                weather.realFeelTemperature.minimum.value = getFloat(getColumnIndexOrThrow(WeatherEntry.COLUMN_MIN_REAL_FEEL_TEMPERATURE))
                weather.realFeelTemperature.minimum.unit = getString(getColumnIndexOrThrow(WeatherEntry.COLUMN_MIN_REAL_FEEL_TEMPERATURE_UNIT))
                weather.realFeelTemperature.maximum.value = getFloat(getColumnIndexOrThrow(WeatherEntry.COLUMN_MAX_REAL_FEEL_TEMPERATURE))
                weather.realFeelTemperature.maximum.unit = getString(getColumnIndexOrThrow(WeatherEntry.COLUMN_MAX_REAL_FEEL_TEMPERATURE_UNIT))
                weather.realFeelTemperatureShade.minimum.value = getFloat(getColumnIndexOrThrow(WeatherEntry.COLUMN_MIN_REAL_FEEL_TEMPERATURE_SHADE))
                weather.realFeelTemperatureShade.minimum.unit = getString(getColumnIndexOrThrow(WeatherEntry.COLUMN_MIN_REAL_FEEL_TEMPERATURE_SHADE_UNIT))
                weather.realFeelTemperatureShade.maximum.value = getFloat(getColumnIndexOrThrow(WeatherEntry.COLUMN_MAX_REAL_FEEL_TEMPERATURE_SHADE))
                weather.realFeelTemperatureShade.maximum.unit = getString(getColumnIndexOrThrow(WeatherEntry.COLUMN_MAX_REAL_FEEL_TEMPERATURE_SHADE_UNIT))
                weather.hoursOfSun = getFloat(getColumnIndexOrThrow(WeatherEntry.COLUMN_HOURS_OF_SUN))

                items.add(weather)
            }
        }

        return items
    }
    fun insertForecast(forecast: DailyForecast, cityId: Int) {
        forecast.forecast.forEach{ insert(it, cityId) }
    }
}