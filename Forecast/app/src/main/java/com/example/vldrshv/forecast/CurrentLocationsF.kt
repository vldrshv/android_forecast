package com.example.vldrshv.forecast

import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import androidx.fragment.app.Fragment
import bg.devlabs.transitioner.Transitioner
import kotlinx.android.synthetic.main.current_locations_fragment.*


class CurrentLocationsF : Fragment() {
    
    private val CLASS_TAG: String = "CurrentLocationsF"
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        
        val rootView: View? = inflater?.inflate(R.layout.current_locations_fragment, container, false)
    
        delay(100) {
            val transition = Transitioner(starting_view, ending_view)
            transition.duration = 5
            transition.interpolator = AccelerateDecelerateInterpolator()
            button.setOnClickListener {
                transition.animateTo(percent = 0f, duration = 2000)//, interpolator = BounceInterpolator())
            }
            
            screen.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> {
                        transition.animateTo(percent = 1f, duration = 1000)
                        true
                    }
                    else -> {
                        true
                    }
                }
            }
            
        }
    
        return rootView
    }
}

fun delay(delay: Long, func: () -> Unit) {
    Handler().postDelayed({
        try {
            func()
        } catch (e: Exception) {
        }
    }, delay)
}