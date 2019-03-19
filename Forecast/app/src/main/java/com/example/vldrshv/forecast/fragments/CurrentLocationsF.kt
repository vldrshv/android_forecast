package com.example.vldrshv.forecast.fragments

import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import bg.devlabs.transitioner.Transitioner
import com.example.vldrshv.forecast.animations.MainAnimation
import com.example.vldrshv.forecast.R

import kotlinx.android.synthetic.main.current_locations_fragment.*

class CurrentLocationsF : Fragment() {
    
    private val CLASS_TAG: String = "CurrentLocationsF"
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // TODO:  find out weather
        activity!!.window.statusBarColor = activity!!.getColor(R.color.colorSnow)
        val rootView: View? = inflater?.inflate(R.layout.current_locations_fragment, container, false)
        val animation: MainAnimation = MainAnimation()
        
        animation.start(animationFunc)
        
        return rootView
    }
    
    private val animationFunc = {
        val transition = Transitioner(starting_view, ending_view)
        transition.duration = 5
        transition.interpolator = AccelerateDecelerateInterpolator()
        button.setOnClickListener {
            transition.animateTo(percent = 0f, duration = 1000)//, interpolator = BounceInterpolator())
            activity!!.window.statusBarColor = activity!!.getColor(R.color.colorSnow)
        }
    
        screen.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    transition.animateTo(percent = 1f, duration = 1000)
                    activity!!.window.statusBarColor = activity!!.getColor(R.color.colorPrimary)
                    true
                }
                else -> {
                    true
                }
            }
        }
    }
}