package com.example.vldrshv.forecast

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView

// TODO:  вставить 3 фрагмента
// TODO:  логика БД для Favourites


class MainActivity : AppCompatActivity() {
    
    lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(mBottomNavViewListener)
    }
    
    private val mBottomNavViewListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_favourite_locations -> println("R.id.action_favorites")
                R.id.action_current_location -> println("action_current_location")
                R.id.action_search_location -> println("action_search_location")
            }
            true
        }
}
