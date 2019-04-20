package com.example.vldrshv.forecast.fragments

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Dialog
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.TRANSLATION_X
import android.view.View.TRANSLATION_Y
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vldrshv.forecast.R
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import com.example.vldrshv.forecast.DailyForecast
import com.example.vldrshv.forecast.Location
import com.example.vldrshv.forecast.adapters.LocationListAdapter
import com.example.vldrshv.forecast.dao.LocationDataSource
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
    private var locationList: ArrayList<Location>? = arrayListOf()
    private var onClickListener: (Location) -> Unit = {}
    private var locationDB: LocationDataSource? = null
    
    enum class Moving { UP, DOWN, INIT }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater?.inflate(R.layout.search_locations_fragment, container, false)
        activity!!.window.statusBarColor = activity!!.getColor(R.color.colorPrimary)

        locationDB = LocationDataSource(activity!!)
        initRecyclerView()
        
        searchView = activity!!.findViewById(com.example.vldrshv.forecast.R.id.starting_view)
        createLocationService()
        var searchTv = activity!!.findViewById(R.id.searchTv) as EditText
        searchTv.text.clear()

        searchTv.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            
            }
            //todo add async or another thread
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length!! >= 3) {
                    val searchCityTask: SearchCityTask = SearchCityTask()
                    searchCityTask.executeLocationSearch(s.toString())
                }
            }
        })
        
        return rootView
    }
    
    private fun createLocationService() {
        locationService = LocationService()
//        val df: DailyForecast? = locationService?.service?.getDailyForecast("294021")?.execute()?.body()
//        Log.i(CLASS_TAG, "${df == null}" )

    }
    
    private fun initRecyclerView() {
        searchingList = rootView!!.findViewById(R.id.searchingList) as RecyclerView
        searchingList!!.layoutManager = LinearLayoutManager(activity!!)
        setRecyclerViewListeners()
        searchingList!!.adapter = LocationListAdapter(locationList!!, activity!!, onClickListener) //{ println{"${it.id}"} }

    }
    
    private fun setRecyclerViewListeners() {
        searchingList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                checkAndChangeState(dy)
                if (isStateChanged() && isStateChanged()) {
                    Log.i(CLASS_TAG, "animation")
                    t = Thread(Runnable(animation))
                    t!!.run()
                }
            }
        })
        onClickListener = {
            var dialog = AlertDialog.Builder(activity!!).setTitle("Add to favourites?")
                    .setPositiveButton("Yes") {
                        dialog: DialogInterface, which: Int ->
                        run {
                            it.isSearched = true
                            it.isFavourite = true
                            Log.i(CLASS_TAG, it.toString())
                            locationDB?.insert(it)
                        }
                    }
                    .setNegativeButton("No") {
                        dialog: DialogInterface, which: Int -> dialog.dismiss()
                    }
                    .show()
        }
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

    inner class SearchCityTask : AsyncTask<CharSequence?, Unit, Unit>(){
        override fun doInBackground(vararg params: CharSequence?) {
            executeLocationSearch(params[0].toString())
        }

        fun executeLocationSearch(s: String) {
            Log.i(CLASS_TAG, s)
            locationList!!.clear()
            locationList!!.add(Location())
            val newLocationList: List<Location>? = locationService?.service?.getLocationsList(s)?.execute()?.body()
            if (newLocationList != null)
                locationList!!.addAll(newLocationList!!)

            Log.i(CLASS_TAG, "locationList.size = ${locationList?.size ?: 0}")
            if (locationList != null) {
                searchingList!!.adapter = LocationListAdapter(locationList!!, activity!!, onClickListener)
                searchingList?.adapter?.notifyDataSetChanged()
            }
        }
    }
    
}
