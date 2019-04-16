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
import com.example.vldrshv.forecast.adapters.SharedPreferencesAdapter

import com.example.vldrshv.forecast.dao.WeatherDataSource
import com.example.vldrshv.forecast.service.LocationService

import kotlinx.android.synthetic.main.current_locations_fragment.*

class CurrentLocationsF : Fragment() {
    
    private val CLASS_TAG: String = "CurrentLocationsF"
    private val SEARCHING_LOCATION = "Updating location..."
    private var locationService: LocationService? = null
    private var spAdapter: SharedPreferencesAdapter? = null
    private var cityEng: String = ""
    private var dailyForecast: DailyForecast? = null

    private var weatherDB: WeatherDataSource? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // TODO:  find out weather
        activity!!.window.statusBarColor = activity!!.getColor(R.color.colorSnow)
        val rootView: View? = inflater!!.inflate(R.layout.current_locations_fragment, container, false)

        spAdapter = SharedPreferencesAdapter(context!!)
        weatherDB = WeatherDataSource(context!!)

        cityEng = spAdapter!!.getString("cityEng")
        val textViewCity = rootView!!.findViewById(R.id.textViewCity) as TextView
        textViewCity.text =  if (cityEng == "") SEARCHING_LOCATION else cityEng

        val animation: MainAnimation = MainAnimation()
        animation.start(animationFunc)

        initLocationService()
        
        return rootView
    }
    
    private val animationFunc = {
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

    private fun initLocationService() {
        locationService = LocationService()
        Log.i(CLASS_TAG, "location service created")
        val locationList: List<Location>? = locationService!!.service.getLocationsList(cityEng).execute().body()
        Log.i(CLASS_TAG, "sent user city, response.list.length = ${locationList?.size}")
        if (locationList != null && locationList.size == 1) {
            getForecast(locationList[0])
        }
    }
    private fun getForecast(location: Location) {
        Log.i(CLASS_TAG, "get forecast for location.id = ${location.id}")
        dailyForecast = locationService!!.service.getDailyForecast(location.id).execute().body()
        Log.i(CLASS_TAG, "forecast response = ${dailyForecast?.forecast?.size}")
        if (dailyForecast != null) // insert weather
            weatherDB!!.insertForecast(dailyForecast!!, location.id)

        // todo insert external info
        // todo insert airAndPollen
    }

}