package com.example.vldrshv.forecast

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


interface LocationI {
    @GET("?apikey=$API_KEY&format=$FORMAT&lang=$RESPONSE_LANG&geocode={lng},{lat}")
    fun getCityByLocation(@Path("lng") lng: Float, @Path("lat") lat: Float): Call<List<String>>

    @GET("group/{id}/users")
    fun groupList(@Path("id") groupId: Int): Call<List<String>>

    private companion object LocationAPI{
        const val API_KEY: String = "2f2c12bf-2712-439f-822a-fe10803d4ced"
        const val FORMAT: String = "json"
        const val RESPONSE_LANG = "en_RU"
    }

    object Factory {
        fun create(): LocationI {
            val retrofit = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://geocode-maps.yandex.ru/1.x/")
                    .build()

            return retrofit.create(LocationI::class.java)
        }
    }
}