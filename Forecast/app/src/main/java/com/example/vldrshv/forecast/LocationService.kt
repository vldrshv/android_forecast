package com.example.vldrshv.forecast

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import retrofit2.Retrofit

class LocationService {
    var service: LocationI = LocationI.Factory.create()

    fun getLocationFromJson(jsonString: String) : Location? {
        var location: Location = Location()
        val jelement: JsonElement = JsonParser().parse(jsonString)
        val jobject = jelement.asJsonObject

        location.city = jobject.get("LocalizedName").asString
        val geoPosition = jobject.get("GeoPosition").asJsonObject
        location.lat = geoPosition.get("Latitude").asFloat
        location.lng = geoPosition.get("Longitude").asFloat

        return location
    }
}