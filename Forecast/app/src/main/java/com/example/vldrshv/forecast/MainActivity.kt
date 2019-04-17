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
import android.view.View
import com.example.vldrshv.forecast.adapters.SharedPreferencesAdapter
import com.example.vldrshv.forecast.dao.LocationDataSource
import com.example.vldrshv.forecast.service.LocationService

import kotlinx.android.synthetic.main.activity_main.*
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast


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

    enum class Fragments {
        F_CURRENT_LOCATION, F_SEARCH_LOCATION, F_FAVOURITE_LOCATION
    }
    private var fragmentState: Fragments = Fragments.F_CURRENT_LOCATION

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        this.savedInstanceState = savedInstanceState
        
        initVariables()
        startLocationService()
    }
    
    private fun initVariables() {
        spAdapter = SharedPreferencesAdapter(this)
        currentCity = spAdapter!!.getString("city")
    
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(mBottomNavViewListener)
        bottomNavigationView.selectedItemId = R.id.action_current_location
        addFragment(CurrentLocationsF())
        fragmentState = Fragments.F_CURRENT_LOCATION
    }

    private fun startLocationService() {
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
    }

    private val mBottomNavViewListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.action_favourite_locations -> {
                        addFragment(FavouriteLocationsF())
                        starting_view.visibility = View.GONE
                        fragmentState = Fragments.F_FAVOURITE_LOCATION
                    }
                    R.id.action_current_location -> {
                        addFragment(CurrentLocationsF())
                        starting_view.visibility = View.GONE
                        fragmentState = Fragments.F_CURRENT_LOCATION
                    }
                    R.id.action_search_location -> {
                        addFragment(SearchLocationsF())
                        starting_view.visibility = View.VISIBLE
                        fragmentState = Fragments.F_SEARCH_LOCATION
                    }
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
        spAdapter!!.putString("cityEng", currentLocation!!.cityEng)
                .putString("cityRus", currentLocation!!.localizedName)
                .putFloat("lat", currentLocation!!.geoposition.lat)
                .putFloat("lng", currentLocation!!.geoposition.lng)
                .putInt("id", currentLocation!!.id)
    }
    private fun saveDataToLocationDB() {
        val locationDB: LocationDataSource = LocationDataSource(this);
        locationDB.insert(currentLocation!!)
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
                locationManager!!.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 0, 0f, this)
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
            Log.i(CLASS_TAG, "Latitude:$lat, Longitude:$lng")
            if (hasInternetConnection()) {
                lat = location.latitude.toFloat()
                lng = location.longitude.toFloat()
                currentLocation = locationService!!.service.getLocationJson("$lat,$lng").execute().body()
            } else
                Toast.makeText(this, "Check internet connection", Toast.LENGTH_LONG).show()

            if (currentLocation != null && currentCity != currentLocation!!.cityEng) {
                saveDataToSharedPref() // save context
                saveDataToLocationDB() // save to locationDB for faster accessing
                // todo add location change broadcast
                if (fragmentState == Fragments.F_CURRENT_LOCATION)
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

    private fun hasInternetConnection(): Boolean {
        val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifi.isConnected || mobile.isConnected
    }
}
