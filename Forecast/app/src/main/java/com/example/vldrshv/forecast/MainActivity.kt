package com.example.vldrshv.forecast

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.location.LocationManager
import android.util.Log
import android.content.pm.PackageManager
import android.location.LocationListener
import androidx.core.app.ActivityCompat

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.vldrshv.forecast.fragments.CurrentLocationsF
import com.example.vldrshv.forecast.fragments.FavouriteLocationsF
import com.example.vldrshv.forecast.fragments.SearchLocationsF
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.os.StrictMode
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import bg.devlabs.transitioner.Transitioner
import com.example.vldrshv.forecast.animations.MainAnimation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.search_locations_fragment.*
//import android.R
import androidx.appcompat.widget.Toolbar


// TODO:  логика БД для Favourites

class MainActivity : AppCompatActivity(), LocationListener {
    
    private val CLASS_TAG: String = "MainActivity"
    private val MY_PERMISSIONS_REQUEST_LOCATION = 1
    
    private var savedInstanceState: Bundle? = null
    private var locationManager: LocationManager? = null
    private var provider: String? = null
    
    lateinit var bottomNavigationView: BottomNavigationView

    private var locationService: LocationService? = null
    private var currentLocation: com.example.vldrshv.forecast.Location? = null
    private var lat: Float = 0f
    private var lng: Float = 0f
    private var currentCity: String = ""

    private var spAdapter: SharedPreferencesAdapter? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.savedInstanceState = savedInstanceState
        spAdapter = SharedPreferencesAdapter(this)
        currentCity = spAdapter!!.getString("city")
        
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(mBottomNavViewListener)
        bottomNavigationView.selectedItemId = R.id.action_current_location
        addFragment(CurrentLocationsF())

        locationService = LocationService()
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        if (rc == PackageManager.PERMISSION_GRANTED) {
            getLocation()
        } else {
            checkLocationPermission()
            getLocation()
        }

//        val toolbar = findViewById<Toolbar>(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        supportActionBar!!.hide()

        val animation: MainAnimation = MainAnimation()
        animation.start(animationFunc)

    }
    
    private val mBottomNavViewListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.action_favourite_locations -> addFragment(FavouriteLocationsF())
                    R.id.action_current_location -> addFragment(CurrentLocationsF())
                    R.id.action_search_location -> addFragment(SearchLocationsF())
                }
                true
            }
    
    private fun addFragment(fragment: Fragment) {
        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right)

            transaction.replace(R.id.flContext, fragment).commit()
        }
    }

    private fun saveDataToSharedPref() {
        spAdapter!!.putString("city", currentLocation!!.city)
                .putFloat("lat", currentLocation!!.lat)
                .putFloat("lng", currentLocation!!.lng)
    }
    private val animationFunc = {
        val transition = Transitioner(starting_view, ending_view)
        transition.duration = 5
        transition.interpolator = AccelerateDecelerateInterpolator()
//        screen.setOnTouchListener { v, event ->
//            if (event.action == MotionEvent.ACTION_DOWN) {
//                transition.animateTo(percent = 0f, duration = 1000)
//                searchImage.setColorFilter(activity!!.getColor(R.color.colorMenuNotSelected))
//                val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
//                imm!!.hideSoftInputFromWindow(v.windowToken, 0)
//                true
//            }
//            false
//        }
        searchingList.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                transition.animateTo(percent = 0f, duration = 1000)
                searchImage.setColorFilter(getColor(R.color.colorMenuNotSelected))
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.hideSoftInputFromWindow(v.windowToken, 0)
                true
            }
            false
        }
        searchTv.setOnTouchListener{v, event ->//TouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                transition.animateTo(percent = 1f, duration = 1000)
                searchImage.setColorFilter(getColor(R.color.colorMenuSelected))
                true
            }
            false
        }
    }
    
    /**
     * ========================================================================================
     * LOCATION METHODS
     * ========================================================================================
     */
    
    private fun isGPSUpdated() : Boolean = lat != 0f && lng != 0f
    
    private fun checkLocationPermission() {
        val rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        Log.i(CLASS_TAG, rc.toString() + "")
        if (rc == PackageManager.PERMISSION_GRANTED) {
            getLocation()
        } else {
            requestGpsPermission()
            getLocation()
        }
    }
    
    private fun getLocation() {
        Log.i(CLASS_TAG, "HANDLE")
        val rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (rc == PackageManager.PERMISSION_GRANTED ) {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            
            if (!isGPSUpdated()) {
                Log.i(CLASS_TAG, "LOCATION CHECK lat = $lat, lng = $lng")
                locationManager!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 0, 0f, this)
//                locationManager!!.requestLocationUpdates(
//                        LocationManager.GPS_PROVIDER, 0, 0f, this)
            }
        }
    }
    
    private fun requestGpsPermission() {
        Log.w(CLASS_TAG, "GPS permission is not granted. Requesting permission")
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST_LOCATION)
        }
    }
    
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation()
                }
            }
        }
    }
    
    override fun onLocationChanged(location: Location) {
        if (!isGPSUpdated()) {
            lat = location.latitude.toFloat()
            lng = location.longitude.toFloat()

            Log.i(CLASS_TAG, "Latitude:$lat, Longitude:$lng")
            val response = locationService!!.service.getLocationJson("$lat,$lng").execute().body()

            val jsonResponse = response!!.string()
            //Log.i(CLASS_TAG, jsonResponse)
            currentLocation = locationService!!.getLocationFromJson(jsonResponse)
            Log.i(CLASS_TAG, currentLocation.toString())

            if (currentCity != currentLocation!!.city) {
                saveDataToSharedPref()
                addFragment(CurrentLocationsF())
            }
        }
    }
    
    override fun onProviderDisabled(provider: String) {
        Log.i("Latitude", "disable")
    }
    
    override fun onProviderEnabled(provider: String) {
        Log.i("Latitude", "enable")
    }
    
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        Log.i("Latitude", "status")
    }
}






