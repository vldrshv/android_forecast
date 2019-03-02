package com.example.vldrshv.forecast

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
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY
import androidx.core.content.ContextCompat.getSystemService



class SearchLocationsF : Fragment() {
    private val CLASS_TAG = "SearchLocationsF"
    
    private var rootView: View? = null
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater?.inflate(R.layout.search_locations_fragment, container, false)
        activity!!.window.statusBarColor = activity!!.getColor(R.color.colorPrimary)
        
        val animation: MainAnimation = MainAnimation()
        
        animation.start(animationFunc)
        return rootView
    }
    
    private val animationFunc = {
        val transition = Transitioner(starting_view, ending_view)
        transition.duration = 5
        transition.interpolator = AccelerateDecelerateInterpolator()
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