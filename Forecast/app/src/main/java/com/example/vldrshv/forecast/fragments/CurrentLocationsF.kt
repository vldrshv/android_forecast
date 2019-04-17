package com.example.vldrshv.forecast.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.fragment.app.Fragment
import bg.devlabs.transitioner.Transitioner
import com.example.vldrshv.forecast.DailyForecast
import com.example.vldrshv.forecast.Location
import com.example.vldrshv.forecast.animations.MainAnimation
import com.example.vldrshv.forecast.R
import com.example.vldrshv.forecast.Weather
import com.example.vldrshv.forecast.adapters.SharedPreferencesAdapter

import com.example.vldrshv.forecast.dao.WeatherDataSource
import com.example.vldrshv.forecast.service.LocationService

import kotlinx.android.synthetic.main.current_locations_fragment.*

class CurrentLocationsF : Fragment() {
    
    private val CLASS_TAG: String = "CurrentLocationsF"
    private val SEARCHING_LOCATION = "Updating location..."
    private var locationService: LocationService? = null
    private var spAdapter: SharedPreferencesAdapter? = null
    private var weatherDB: WeatherDataSource? = null

    private var dailyForecast: DailyForecast? = null
    private var currentLocation: Location = Location()
    private var locationWeather: ArrayList<Weather>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity!!.window.statusBarColor = activity!!.getColor(R.color.colorSnow)
        val rootView: View? = inflater!!.inflate(R.layout.current_locations_fragment, container, false)

        initSharedPrefAndDB()
        getCityFromSharedPref()
        initAnimation()
        initLocationService()
        checkWeatherExists()
        if (!currentLocation.isNull())
            initUI(rootView)

        return rootView
    }

    private fun initSharedPrefAndDB() {
        spAdapter = SharedPreferencesAdapter(context!!)
        weatherDB = WeatherDataSource(context!!)
    }
    private fun getCityFromSharedPref() {
        currentLocation.cityEng = spAdapter!!.getString("cityEng")
        currentLocation.localizedName = spAdapter!!.getString("cityRus")
        currentLocation.geoposition.lat = spAdapter!!.getFloat("lat")
        currentLocation.geoposition.lng = spAdapter!!.getFloat("lng")
        currentLocation.id = spAdapter!!.getInt("id")
    }
    private fun initAnimation() {
        val animation: MainAnimation = MainAnimation()
        val animationFunc = {
            val transition = Transitioner(starting_view_location, ending_view_location)
            transition.duration = 5
            transition.interpolator = AccelerateDecelerateInterpolator()
            button.setOnClickListener {
                transition.animateTo(percent = 0f, duration = 1000)//, interpolator = BounceInterpolator())
                activity!!.window.statusBarColor = activity!!.getColor(R.color.colorSnow)
            }

            screen.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> {
                        transition.animateTo(percent = 1f, duration = 1000)
                        activity!!.window.statusBarColor = activity!!.getColor(R.color.colorPrimary)
                        true
                    }
                    else -> {
                        true
                    }
                }
            }
        }
        animation.start(animationFunc)
    }

    private fun checkWeatherExists() {
//        if (!currentLocation.isNull()) {
        Log.i(CLASS_TAG, "check weather exists in weatherDB")
            locationWeather = getWeatherFromDB() as ArrayList<Weather>?
            if (locationWeather!!.isEmpty())
                locationWeather = getForecast(currentLocation) as ArrayList<Weather>?
//        }
        Log.i(CLASS_TAG, "locationWeather.size = ${locationWeather?.size}")
    }

    // TODO:  find out weather
    private fun initLocationService() {
        locationService = LocationService()
        Log.i(CLASS_TAG, "location service created")
    }
    private fun getForecast(location: Location): List<Weather>? {
        Log.i(CLASS_TAG, "get forecast for location.id = ${location.id}")
        if (!location.isNull()) {
            dailyForecast = locationService!!.service.getDailyForecast(location.id).execute().body()
            Log.i(CLASS_TAG, "forecast response = ${dailyForecast?.forecast?.size}")
            if (dailyForecast != null) { // insert weather
                weatherDB!!.insertForecast(dailyForecast!!, location.id)
                return dailyForecast!!.forecast
            }

            // todo insert external info
            // todo insert airAndPollen
        }
        return null
    }

    private fun getWeatherFromDB(): List<Weather> {
        return weatherDB!!.select(currentLocation.id)
    }

    private fun initUI(rootView: View?) {
        val textViewCity = rootView!!.findViewById(R.id.textViewCity) as TextView
        textViewCity.text =  if (currentLocation.cityEng == "") SEARCHING_LOCATION else currentLocation.cityEng

        val textViewTemperature = rootView!!.findViewById(R.id.textViewTemperature) as TextView
        val avgTemp = (locationWeather!!.get(0).temperature.minimum.value.toInt() + locationWeather!!.get(0).temperature.maximum.value.toInt()) / 2
        textViewTemperature.text =
                String.format("%d°%s", avgTemp, locationWeather!!.get(0).temperature.maximum.unit)
    }
}