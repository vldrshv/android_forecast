package com.example.vldrshv.forecast

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.view.WindowManager
import android.os.Build
import android.support.v4.app.Fragment


// TODO:  вставить 3 фрагмента
// TODO:  логика БД для Favourites


class MainActivity : AppCompatActivity() {
    
    var savedInstanceState: Bundle? = null
    
    lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.savedInstanceState = savedInstanceState
        
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(mBottomNavViewListener)
        bottomNavigationView.selectedItemId = R.id.action_current_location
        addFragment(CurrentLocationsF())
        
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
                    .replace(R.id.flContext, fragment, "rageComicList")
                    .commit()
        }
    }
}
