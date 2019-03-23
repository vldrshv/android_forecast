package com.example.vldrshv.forecast.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import bg.devlabs.transitioner.Transitioner

import kotlinx.android.synthetic.main.search_locations_fragment.*
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vldrshv.forecast.animations.MainAnimation
import com.example.vldrshv.forecast.R
import com.example.vldrshv.forecast.adapters.FavouriteListAdapter


class SearchLocationsF : Fragment() {
    private val CLASS_TAG = "SearchLocationsF"
    
    private var rootView: View? = null
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater?.inflate(R.layout.search_locations_fragment, container, false)
        activity!!.window.statusBarColor = activity!!.getColor(R.color.colorPrimary)
        
        val animation: MainAnimation = MainAnimation()
        animation.start(animationFunc)

        val searchingList = rootView!!.findViewById(R.id.searchingList) as RecyclerView
        searchingList!!.layoutManager = LinearLayoutManager(activity!!)
        searchingList!!.adapter = FavouriteListAdapter()

        return rootView
    }
    
    private val animationFunc = {
        val transition = Transitioner(starting_view, ending_view)
        transition.duration = 5
        transition.interpolator = AccelerateDecelerateInterpolator()
        searchingList.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                transition.animateTo(percent = 0f, duration = 1000)
                searchImage.setColorFilter(activity!!.getColor(R.color.colorMenuNotSelected))
                val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.hideSoftInputFromWindow(v.windowToken, 0)
                true
            }
            false
        }
        screen.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                transition.animateTo(percent = 0f, duration = 1000)
                searchImage.setColorFilter(activity!!.getColor(R.color.colorMenuNotSelected))
                val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.hideSoftInputFromWindow(v.windowToken, 0)
                true
            }
            false
        }
        searchTv.setOnTouchListener{v, event ->//TouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                transition.animateTo(percent = 1f, duration = 1000)
                searchImage.setColorFilter(activity!!.getColor(R.color.colorMenuSelected))
                true
            }
            false
        }
    }
    
}