package com.example.vldrshv.forecast

class Location() {
    var id: Int = 0

    var city: String? = ""

    var lat: Float = 0f
    var lng: Float = 0f

    override fun toString(): String {
        return "Location(id=$id, city='$city', lat=$lat, lng=$lng)"
    }

}