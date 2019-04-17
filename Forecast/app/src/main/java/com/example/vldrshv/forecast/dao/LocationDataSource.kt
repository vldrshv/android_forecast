package com.example.vldrshv.forecast.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.example.vldrshv.forecast.Location

class LocationDataSource(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    object LocationEntry : BaseColumns {
        const val TABLE_NAME = "location"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_CITY_RUS = "cityRus"
        const val COLUMN_NAME_CITY_ENG = "cityEng"
        const val COLUMN_NAME_COUNTRY_ID = "countryId"
        const val COLUMN_NAME_COUNTRY = "countryName"
        const val COLUMN_NAME_LAT = "latitude"
        const val COLUMN_NAME_LNG = "longitude"
        const val IS_SEARCHED = "is_searched"
        const val IS_FAVOURITE = "is_favourite"
    }

    private val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${LocationEntry.TABLE_NAME} (" +
                    "${LocationEntry.COLUMN_NAME_ID} INTEGER PRIMARY KEY UNIQUE," +
                    "${LocationEntry.COLUMN_NAME_CITY_RUS} TEXT," +
                    "${LocationEntry.COLUMN_NAME_CITY_ENG} TEXT," +
                    "${LocationEntry.COLUMN_NAME_COUNTRY_ID} TEXT," +
                    "${LocationEntry.COLUMN_NAME_COUNTRY} TEXT," +
                    "${LocationEntry.COLUMN_NAME_LAT} TEXT," +
                    "${LocationEntry.COLUMN_NAME_LNG} TEXT," +
                    "${LocationEntry.IS_SEARCHED} INTEGER," +
                    "${LocationEntry.IS_FAVOURITE} INTEGER)"

    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${LocationEntry.TABLE_NAME}"

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
        const val DATABASE_NAME = "LocationDataSource.db"
    }

    fun insert(location: Location){
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(LocationEntry.COLUMN_NAME_ID, location.id)
            put(LocationEntry.COLUMN_NAME_CITY_RUS, location.localizedName)
            put(LocationEntry.COLUMN_NAME_CITY_ENG, location.cityEng)
            put(LocationEntry.COLUMN_NAME_COUNTRY_ID, location.country.id)
            put(LocationEntry.COLUMN_NAME_COUNTRY, location.country.name)
            put(LocationEntry.COLUMN_NAME_LAT, location.geoposition.lat)
            put(LocationEntry.COLUMN_NAME_LNG, location.geoposition.lng)
            put(LocationEntry.IS_FAVOURITE, if (location.isFavourite) 1 else 0)
            put(LocationEntry.IS_SEARCHED, if (location.isSearched) 1 else 0)
        }

        db?.replace(LocationEntry.TABLE_NAME, null, values)
    }

    fun selectAll(): ArrayList<Location> {
        val db = this.readableDatabase

        val cursor = db.query(
                LocationEntry.TABLE_NAME,              // The table to query
                null, //projection,           // The array of columns to return (pass null to get all)
                null, //selection,            // The columns for the WHERE clause
                null, //selectionArgs,     // The values for the WHERE clause
                null,                          // don't group the rows
                null,                           // don't filter by row groups
                null                           // The sort order
        )
        val items = arrayListOf<Location>()
        with(cursor) {
            while (moveToNext()) {
                val location = Location()
                location.id = getInt(getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_ID))
                location.localizedName = getString(getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_CITY_RUS))
                location.cityEng = getString(getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_CITY_ENG))
                location.country.id = getString(getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_COUNTRY_ID))
                location.country.name = getString(getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_COUNTRY))
                location.geoposition.lat = getFloat(getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_LAT))
                location.geoposition.lng = getFloat(getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_LNG))
                location.isSearched = getInt(getColumnIndexOrThrow(LocationEntry.IS_SEARCHED)) == 1
                location.isFavourite = getInt(getColumnIndexOrThrow(LocationEntry.IS_FAVOURITE)) == 1

                items.add(location)
            }
        }
        return items
    }
    
    private fun deleteAll() {
        val db = this.writableDatabase
        db?.delete(LocationEntry.TABLE_NAME, null, null)
    }

    private fun update(location: Location) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(LocationEntry.COLUMN_NAME_ID, location.id)
            put(LocationEntry.COLUMN_NAME_CITY_RUS, location.localizedName)
            put(LocationEntry.COLUMN_NAME_CITY_ENG, location.cityEng)
            put(LocationEntry.COLUMN_NAME_COUNTRY_ID, location.country.id)
            put(LocationEntry.COLUMN_NAME_COUNTRY, location.country.name)
            put(LocationEntry.COLUMN_NAME_LAT, location.geoposition.lat)
            put(LocationEntry.COLUMN_NAME_LNG, location.geoposition.lng)
            put(LocationEntry.IS_FAVOURITE, if (location.isFavourite) 1 else 0)
            put(LocationEntry.IS_SEARCHED, if (location.isSearched) 1 else 0)
        }
        db?.update(LocationEntry.TABLE_NAME, values, "${LocationEntry.COLUMN_NAME_ID} = ${location.id}", null)
    }
}

