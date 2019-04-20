package com.example.vldrshv.forecast

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

//2019-04-17T05:21:00+03:00
class DateFormatter(var dateCurrent: String = "") {
    var formatString: String = "yyyy-MM-dd'T'hh:mm:ss"
    var day: String = ""
    var month: String = ""
    var year: String = ""
    var hours: String = ""
    var minutes: String = ""
    var timezone: String = ""

    private val DAY_OFFSET = 24*60*60*1000 // Hour * Min * Sec * MillSec

    init {
        getDate()
    }

    private fun getDate() {
        if (dateCurrent != "") {
            val dateFormat = SimpleDateFormat(formatString)
            val dateFormated = dateFormat.parse(dateCurrent)
            format(dateFormated)
        }
    }
    private fun format(date: Date) {
        day = if (date.date < 10) "0${date.date}" else "${date.date}"
        month = if (date.month < 10) "0${date.month}" else "${date.month}"
        year = "${date.year + 1900}"
        hours = if (date.hours < 10) "0${date.hours}" else "${date.hours}"
        minutes = if (date.minutes < 10) "0${date.minutes}" else "${date.minutes}"
        timezone = dateCurrent.substring(19)
    }

    fun getDate(date: String): Date? {
        if (date != "") {
            val dateFormat = SimpleDateFormat(formatString)
            return dateFormat.parse(date)
        }
        return null
    }

    fun getTimeFormat(): String = "$hours:$minutes"
    fun getDayMonth(): String = "$day.$month"

    fun compare(date: DateFormatter): Int {
        val thisDate: String = "$year-$month-$day $hours:$minutes"
        val compareTo: String = "${date.year}-${date.month}-${date.day} ${date.hours}:${date.minutes}"

        return thisDate.compareTo(compareTo)
    }
    fun isLessThan(date: Date): Boolean {
        println("isLessThan: " + (this.getDate(dateCurrent)!!.time + DAY_OFFSET) + " < " + date.time)
        return this.getDate(dateCurrent)!!.time + DAY_OFFSET < date.time
    }

    fun equal(date: Date): Boolean {
        return date.compareTo(getDate(dateCurrent)) < 1
    }

    fun isBetween(dateL: DateFormatter, dateR: DateFormatter): Boolean {
        return compare(dateL) != -1 && compare(dateR) != 1
    }
}