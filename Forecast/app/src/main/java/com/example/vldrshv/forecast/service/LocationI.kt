package com.example.vldrshv.forecast.service

import com.example.vldrshv.forecast.Location
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface LocationI {

    @GET("/locations/v1/cities/geoposition/search?apikey=$API_KEY&toplevel=true&language=ru-ru")
    fun getLocationJson(@Query("q") lngLatString: String): Call<Location>

    @GET("locations/v1/cities/autocomplete?apikey=$API_KEY")
    fun getLocationsList(@Query("q") city: String): Call<List<Location>>

    private companion object LocationAPI{
        const val API_KEY: String = "inhxFCga0fsTKrB1aZZqU5jmuLnkbYxM"
    }

    object Factory {
        fun create(): LocationI {
            val retrofit = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://dataservice.accuweather.com")
                    .build()

            return retrofit.create(LocationI::class.java)
        }
    }
}