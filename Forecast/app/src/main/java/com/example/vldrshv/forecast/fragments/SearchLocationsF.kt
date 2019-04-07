package com.example.vldrshv.forecast.fragments

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.TRANSLATION_X
import android.view.View.TRANSLATION_Y
import android.view.ViewGroup
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vldrshv.forecast.R
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import com.example.vldrshv.forecast.Location
import com.example.vldrshv.forecast.adapters.LocationListAdapter
import com.example.vldrshv.forecast.service.LocationService


class SearchLocationsF : Fragment() {
    private val CLASS_TAG = "SearchLocationsF"
    
    private var rootView: View? = null
    private var searchingList: RecyclerView? = null
    private var currState = Moving.INIT
    private var prevState: Moving = Moving.INIT
    
    private var t: Thread? = null
    private var searchView: ConstraintLayout? = null
    private var position: Float = 0f
    private var animator: ObjectAnimator? = null
    
    private var locationService: LocationService? = null
    private var locationList: List<Location>? = listOf()
    
    enum class Moving { UP, DOWN, INIT }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater?.inflate(R.layout.search_locations_fragment, container, false)
        activity!!.window.statusBarColor = activity!!.getColor(R.color.colorPrimary)
        
        initRecyclerView()
        
        searchView = activity!!.findViewById(com.example.vldrshv.forecast.R.id.starting_view)
        createLocationService()
        var searchTv = activity!!.findViewById(R.id.searchTv) as EditText
        searchTv.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            
            }
    
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            
            }
    
            //todo add async or another thread
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length!! >= 3) {
                    locationList = locationService?.service?.getLocationsList(s.toString())?.execute()?.body()
                    Log.i(CLASS_TAG, "size = ${locationList?.size}")
                    Log.i(CLASS_TAG, locationList?.get(0).toString())
                }
                if (locationList != null)
                    searchingList!!.adapter = LocationListAdapter(locationList!!, activity!!)
            }
        })
        
        return rootView
    }
    
    private fun createLocationService() {
        locationService = LocationService()
    }
    
    private fun initRecyclerView() {
        searchingList = rootView!!.findViewById(R.id.searchingList) as RecyclerView
        searchingList!!.layoutManager = LinearLayoutManager(activity!!)
        searchingList!!.adapter = LocationListAdapter(locationList!!, activity!!)
        
        setRecyclerViewScrollListener()
    }
    
    private fun setRecyclerViewScrollListener() {
        searchingList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                checkAndChangeState(dy)
                if (isStateChanged() && isStateChanged()) {
                    Log.i(CLASS_TAG, "animation")
                    t = Thread(Runnable(animation))//animation(currState)
                    t!!.run()
                }
            }
        })
        
    }
    
    var animation = {
        position = if (currState == Moving.DOWN) 0f else -500f
        animator = ObjectAnimator.ofFloat(searchView, TRANSLATION_Y, position)
        animator!!.duration = 500
        animator!!.start()
    }
    
    
    private fun checkAndChangeState(dy: Int) {
        prevState = currState
        if (dy > 0) {
            currState = Moving.UP
        }
        if (dy < 0) {
            currState = Moving.DOWN
        }
        
    }
    
    private fun isStateChanged(): Boolean {
        return prevState != currState
    }
    
}