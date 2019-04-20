package com.example.vldrshv.forecast.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.fragment.app.Fragment
import bg.devlabs.transitioner.Transitioner
import com.example.vldrshv.forecast.animations.MainAnimation
import com.example.vldrshv.forecast.adapters.SharedPreferencesAdapter

import com.example.vldrshv.forecast.dao.WeatherDataSource
import com.example.vldrshv.forecast.service.LocationService

import kotlinx.android.synthetic.main.current_locations_fragment.*
import android.content.IntentFilter
import android.os.AsyncTask
import com.example.vldrshv.forecast.*
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import java.util.*
import kotlin.collections.ArrayList

/**
 * CURRENT LOCATION FRAGMENT
 *
 * Shows user info about the weather un his location
 * - DAILY update (only on entering the app)
 * - storing info to DB
 * - 4 days weather prediction
 * - average temperature (todo #1 make hourly)
 * - average feeling temperature (todo #2 make min/max)
 */

class CurrentLocationsF : Fragment() {
    
    private val CLASS_TAG: String = "CurrentLocationsF"
    private val SEARCHING_LOCATION = "Updating location..."
    private var rootView: View? = null
    private var locationService: LocationService? = null
    private var spAdapter: SharedPreferencesAdapter? = null
    private var weatherDB: WeatherDataSource? = null
    private var broadcastReceiver: BroadcastReceiver? = null

    private var dailyForecast: DailyForecast? = null
    private var currentLocation: Location = Location()
    private var locationWeather: ArrayList<Weather>? = null

    /** INIT BROADCAST RECEIVER AND START FRAGMENT ACTIVITY */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity!!.window.statusBarColor = activity!!.getColor(R.color.colorSnow)
        rootView = inflater!!.inflate(R.layout.current_locations_fragment, container, false)

        initBroadcastReceiver()
        initAnimation()
        initSharedPrefAndDB()
        initLocationService()

        start()

        if (!currentLocation.isNull())
            initUI()

        return rootView
    }

    /** START FRAGMENT'S FUNCTIONALITY*/
    private fun start() {
        getCityFromSharedPref()
        checkWeatherExists()
    }

    /** INITIALIZATION BLOCK :
     *  --  DB,
     *  --  ANIMATION,
     *  --  LOCATION SERVICE,
     *  --  UI,
     *  --  BROADCAST RECEIVER
     */
    private fun initSharedPrefAndDB() {
        spAdapter = SharedPreferencesAdapter(context!!)
        weatherDB = WeatherDataSource(context!!)
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
    private fun initLocationService() {
        locationService = LocationService()
        Log.i(CLASS_TAG, "location service created")
    }
    private fun initUI() {
        val weatherNow = locationWeather!!.get(0)
        val textViewCity = rootView!!.findViewById(R.id.textViewCity) as TextView
        textViewCity.text =  if (currentLocation.cityEng == "") SEARCHING_LOCATION else currentLocation.cityEng

        val textViewTemperature = rootView!!.findViewById(R.id.textViewTemperature) as TextView
        val avgTemp = (weatherNow.temperature.minimum.value.toInt() + weatherNow.temperature.maximum.value.toInt()) / 2
        textViewTemperature.text =
                String.format("%d°%s", avgTemp, weatherNow.temperature.maximum.unit)

//        val imageWeather = rootView!!.findViewById(R.id.imageWeather) as ImageView
//        imageWeather.setImageDrawable(activity!!.getDrawable(getImage(weatherNow)))
        var dateFormatter: DateFormatter = DateFormatter()

        if (weatherNow.sun.riseTime != "") {
            val sunRise = rootView!!.findViewById(R.id.tvSunRise) as TextView
            dateFormatter = DateFormatter(weatherNow.sun.riseTime)
            sunRise.text = dateFormatter.getTimeFormat() // 2019-04-17T05:21:00+03:00
        }
        if (weatherNow.sun.setTime != "") {
            val sunSet = rootView!!.findViewById(R.id.tvSunSet) as TextView
            dateFormatter = DateFormatter(weatherNow.sun.setTime)
            sunSet.text = dateFormatter.getTimeFormat() // 2019-04-17T05:21:00+03:00
        }
        val tvFeelsLike = rootView!!.findViewById(R.id.tvFeelsLike) as TextView
        val avgFeelTemp = (weatherNow.realFeelTemperature.maximum.value +
                weatherNow.realFeelTemperature.minimum.value) / 2
        tvFeelsLike.text = avgFeelTemp.toString()

        initGraph()
    }
    private fun initGraph() {
        val graph = rootView!!.findViewById(R.id.chartview) as GraphView
        graph.removeAllSeries()

        if (locationWeather == null)
            return

        val dataPointsArray: ArrayList<DataPoint> = arrayListOf()
        var dateFormatter: DateFormatter = DateFormatter()

        locationWeather!!.forEach {
            run {
                dateFormatter = DateFormatter(it.date)
                Log.i(CLASS_TAG, "" + dateFormatter.getDate(it.date) )
                dataPointsArray.add(DataPoint(dateFormatter.getDate(it.date), it.temperature.maximum.value.toDouble()))
            }
        }

        val series = LineGraphSeries<DataPoint>(dataPointsArray.toTypedArray())
        graph.addSeries(series)

        graph.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(activity!!)
        graph.gridLabelRenderer.numHorizontalLabels = 4 // only 4 because of the space

        graph.viewport.setMinX(dateFormatter.getDate(locationWeather!![0].date)!!.time.toDouble())
        graph.viewport.setMaxX(dateFormatter.getDate(locationWeather!![4].date)!!.time.toDouble())
        graph.viewport.isXAxisBoundsManual = true

        graph.gridLabelRenderer.setHumanRounding(false)
    }
    private fun initBroadcastReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {
                Log.i(CLASS_TAG, "RECEIVED")
                start()
                initUI()
            }
        }
        val intentFilter = IntentFilter("location_updated")
        activity!!.registerReceiver(broadcastReceiver, intentFilter)

    }

    private fun getCityFromSharedPref() {
        currentLocation.cityEng = spAdapter!!.getString("cityEng")
        currentLocation.localizedName = spAdapter!!.getString("cityRus")
        currentLocation.geoposition.lat = spAdapter!!.getFloat("lat")
        currentLocation.geoposition.lng = spAdapter!!.getFloat("lng")
        currentLocation.id = spAdapter!!.getInt("id")
    }

    private fun checkWeatherExists() {
        val date: Date = Date()
        var dateFormatter = DateFormatter()

        Log.i(CLASS_TAG, "check weather exists in weatherDB")
        locationWeather = getWeatherFromDB() as ArrayList<Weather>?

        if (locationWeather!!.isEmpty()) {
            locationWeather = getForecast(currentLocation) as ArrayList<Weather>?
        } else {
            dateFormatter = DateFormatter(locationWeather!![0].date)
            if (dateFormatter.isLessThan(date)) {
                locationWeather = getForecast(currentLocation) as ArrayList<Weather>?
                if (dailyForecast != null) {
                    weatherDB!!.insertForecast(dailyForecast!!, currentLocation.id)
                }
            }
        }
        Log.i(CLASS_TAG, "locationWeather.size = ${locationWeather?.size}")
    }
    private fun getForecast(location: Location): List<Weather>? {
        Log.i(CLASS_TAG, "get forecast for location.id = ${location.id}")
        if (!location.isNull()) {
            dailyForecast = locationService!!.service.getDailyForecast(location.id).execute().body()
            Log.i(CLASS_TAG, "forecast response = ${dailyForecast?.forecast?.size}")
            if (dailyForecast != null) { // insert weather
                weatherDB!!.insertForecast(dailyForecast!!, location.id)
                return dailyForecast!!.forecast
            } else return weatherDB!!.select(location.id)

            // todo insert external info
            // todo insert airAndPollen
        }
        return null
    }
    private fun getWeatherFromDB(): List<Weather> {
        return weatherDB!!.select(currentLocation.id)
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.unregisterReceiver(broadcastReceiver)
    }
}