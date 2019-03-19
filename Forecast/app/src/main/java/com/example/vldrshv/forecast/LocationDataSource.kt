package com.example.vldrshv.forecast

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class LocationDataSource(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    object LocationEntry : BaseColumns {
        const val TABLE_NAME = "location"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_CITY = "city"
        const val COLUMN_NAME_LAT = "latitude"
        const val COLUMN_NAME_LNG = "longitude"
    }

    private val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${LocationEntry.TABLE_NAME} (" +
                    "${LocationEntry.COLUMN_NAME_ID} INTEGER PRIMARY KEY," +
                    "${LocationEntry.COLUMN_NAME_CITY} TEXT," +
                    "${LocationEntry.COLUMN_NAME_LAT} TEXT)," +
                    "${LocationEntry.COLUMN_NAME_LNG} TEXT)"

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

    fun insert(dbHelper: LocationDataSource, location: Location){
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(LocationEntry.COLUMN_NAME_CITY, location.city)
            put(LocationEntry.COLUMN_NAME_LAT, location.lat)
            put(LocationEntry.COLUMN_NAME_LNG, location.lng)
        }

        db?.insert(LocationEntry.TABLE_NAME, null, values)
    }

    fun selectAll(dbHelper: LocationDataSource): List<Location> {
        val db = dbHelper.readableDatabase
//        val projection = arrayOf(LocationEntry.COLUMN_NAME_ID,
//                LocationEntry.COLUMN_NAME_CITY,
//                LocationEntry.COLUMN_NAME_LAT,
//                LocationEntry.COLUMN_NAME_LNG)
//        val selection = "${LocationEntry.COLUMN_NAME_CITY} = ?"
//        val selectionArgs = arrayOf("My Title")
//        val sortOrder = "${FeedEntry.COLUMN_NAME_SUBTITLE} DESC"

        val cursor = db.query(
                LocationEntry.TABLE_NAME,   // The table to query
                null, //projection,             // The array of columns to return (pass null to get all)
                null, //selection,              // The columns for the WHERE clause
                null, //selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null //sortOrder               // The sort order
        )
        val items = mutableListOf<Location>()
        with(cursor) {
            while (moveToNext()) {
                val location = Location()
                location.id = getInt(getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_ID))
                location.city = getString(getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_CITY))
                location.lat = getFloat(getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_LAT))
                location.lng = getFloat(getColumnIndexOrThrow(LocationEntry.COLUMN_NAME_LNG))

                items.add(location)
            }
        }
        return items
    }
}

