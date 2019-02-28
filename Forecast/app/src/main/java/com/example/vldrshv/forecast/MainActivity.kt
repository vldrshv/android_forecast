package com.example.vldrshv.forecast

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
//import android.support.design.widget.BottomNavigationView
//import androidx.core.app.Fragment
import android.location.LocationManager
import android.util.Log
import android.content.pm.PackageManager
import android.location.LocationListener
import androidx.core.app.ActivityCompat

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


// TODO:  вставить 3 фрагмента
// TODO:  логика БД для Favourites


class MainActivity : AppCompatActivity(), LocationListener {
    
    private val CLASS_TAG: String = "MainActivity"
    private val MY_PERMISSIONS_REQUEST_LOCATION = 1
    
    private var savedInstanceState: Bundle? = null
    private var locationManager: LocationManager? = null
    private var provider: String? = null
    
    lateinit var bottomNavigationView: BottomNavigationView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.savedInstanceState = savedInstanceState
        
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(mBottomNavViewListener)
        bottomNavigationView.selectedItemId = R.id.action_current_location
        addFragment(CurrentLocationsF())
    
        val rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        Log.i(CLASS_TAG, rc.toString() + "")
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
                    R.id.action_favourite_locations -> addFragment(FavouriteLocationsF())
                    R.id.action_current_location -> addFragment(CurrentLocationsF())
                    R.id.action_search_location -> addFragment(SearchLocationsF())
                }
                true
            }
    
    private fun addFragment(fragment: Fragment) {
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.flContext, fragment)
                    .commit()
        }
    }
    
    /**
     * ========================================================================================
     * LOCATION METHODS
     * ========================================================================================
     */
    
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
        if (rc == PackageManager.PERMISSION_GRANTED) {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            
            locationManager!!.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0f, this)
            locationManager!!.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 0, 0f, this)
            
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
        val lat = location.latitude.toFloat()
        val longt = location.longitude.toFloat()
        
        Log.i(CLASS_TAG, "Latitude:$lat, Longitude:$longt")
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






