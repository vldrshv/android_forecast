package com.example.vldrshv.forecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment


class FavouriteLocationsF : Fragment() {
    private val CLASS_TAG = "FavouriteLocationsF"
    
    private var rootView: View? = null
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater?.inflate(R.layout.favourite_locations_fragment, container, false)
        activity!!.window.statusBarColor = activity!!.getColor(R.color.colorPrimary)
        return rootView
    }
    
}