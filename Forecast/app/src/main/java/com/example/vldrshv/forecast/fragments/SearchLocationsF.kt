package com.example.vldrshv.forecast.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vldrshv.forecast.R
import com.example.vldrshv.forecast.adapters.FavouriteListAdapter


class SearchLocationsF : Fragment() {
    private val CLASS_TAG = "SearchLocationsF"
    
    private var rootView: View? = null
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater?.inflate(R.layout.search_locations_fragment, container, false)
        activity!!.window.statusBarColor = activity!!.getColor(R.color.colorPrimary)
        //(activity!! as AppCompatActivity).supportActionBar!!.show()

        val searchingList = rootView!!.findViewById(R.id.searchingList) as RecyclerView
        searchingList!!.layoutManager = LinearLayoutManager(activity!!)
        searchingList!!.adapter = FavouriteListAdapter()

        return rootView
    }
    

    
}