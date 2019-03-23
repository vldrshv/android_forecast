package com.example.vldrshv.forecast

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.*


interface LocationI {

    @GET("/locations/v1/cities/geoposition/search?apikey=$API_KEY&toplevel=true")
    fun getLocationJson(@Query("q") lngLatString: String): Call<ResponseBody>

    @GET("group/{id}/users")
    fun groupList(@Path("id") groupId: Int): Call<List<String>>

    private companion object LocationAPI{
//        const val API_KEY: String = "2f2c12bf-2712-439f-822a-fe10803d4ced"
//        const val FORMAT: String = "json"
//        const val RESPONSE_LANG = "en_RU"

        const val API_KEY: String = "inhxFCga0fsTKrB1aZZqU5jmuLnkbYxM"

    }

    object Factory {
        fun create(): LocationI {
            val retrofit = Retrofit.Builder()
//                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://dataservice.accuweather.com")
                    .build()

            return retrofit.create(LocationI::class.java)
        }
    }
}