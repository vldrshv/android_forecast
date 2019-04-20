package com.example.vldrshv.forecast.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vldrshv.forecast.Location
import com.example.vldrshv.forecast.R
import com.example.vldrshv.forecast.adapters.LocationListAdapter
import com.example.vldrshv.forecast.dao.LocationDataSource


class FavouriteLocationsF : Fragment() {
    private val CLASS_TAG = "FavouriteLocationsF"
    
    private var rootView: View? = null
    private var favouritesList: RecyclerView? = null
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater?.inflate(R.layout.favourite_locations_fragment, container, false)
        activity!!.window.statusBarColor = activity!!.getColor(R.color.colorPrimary)

        val locationDB: LocationDataSource = LocationDataSource(activity!!)
        val listLocations: ArrayList<Location> = locationDB.selectAll()//isFavourite = true)

        favouritesList = rootView!!.findViewById(R.id.favouritesList) as RecyclerView
        favouritesList!!.layoutManager = LinearLayoutManager(activity!!)
        favouritesList!!.adapter = LocationListAdapter(listLocations, activity!!) { println(it.id)}

        return rootView
    }
    
}