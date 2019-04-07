package com.example.vldrshv.forecast

import com.google.gson.annotations.SerializedName

class Location() {
    @SerializedName("Key")
    var id: Int = 0
    
    @SerializedName("LocalizedName")
    var cityRus: String? = ""
    
    @SerializedName("EnglishName")
    var cityEng: String = ""
    
    @SerializedName("Country")
    var country: Country = Country()
    
    @SerializedName("GeoPosition")
    var geoposition: Geoposition = Geoposition()

    override fun toString(): String {
        return "Location(id=$id, city='$cityRus', cityEng='$cityEng', country=$country , geoposition=$geoposition)"
    }

}

class Country {
    @SerializedName("ID")
    var id: String = ""
    @SerializedName("LocalizedName")
    var name: String = ""
    
    override fun toString(): String {
        return "Country(id='$id', countruName='$name')"
    }
    
    
}

class Geoposition {
    @SerializedName("Latitude")
    var lat: Float = 0f
    @SerializedName("Longitude")
    var lng: Float = 0f
    
    override fun toString(): String {
        return "Geoposition(lat=$lat, lng=$lng)"
    }
    
    
}