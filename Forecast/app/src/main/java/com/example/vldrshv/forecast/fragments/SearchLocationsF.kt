package com.example.vldrshv.forecast.fragments

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.TRANSLATION_X
import android.view.View.TRANSLATION_Y
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vldrshv.forecast.R
import com.example.vldrshv.forecast.adapters.FavouriteListAdapter
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING


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

    enum class Moving { UP, DOWN, INIT }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater?.inflate(R.layout.search_locations_fragment, container, false)
        activity!!.window.statusBarColor = activity!!.getColor(R.color.colorPrimary)

        initRecyclerView()

        searchView = activity!!.findViewById(com.example.vldrshv.forecast.R.id.starting_view)

        return rootView
    }

    private fun initRecyclerView() {
        searchingList = rootView!!.findViewById(R.id.searchingList) as RecyclerView
        searchingList!!.layoutManager = LinearLayoutManager(activity!!)
        searchingList!!.adapter = FavouriteListAdapter()

        setRecyclerViewScrollListener()
    }
    private fun setRecyclerViewScrollListener() {
        searchingList!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                checkAndChangeState(dy)
                if (isStateChanged() && isStateChanged()) {
                    Log.i(CLASS_TAG, "animation")
                    t = Thread(Runnable(animation))//animation(currState)
                    t!!.run()
                    Log.i(CLASS_TAG, "currState = $currState <-- prevState = $prevState : position = $position")

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

    private fun isStateChanged() : Boolean {
        return prevState != currState
    }

}