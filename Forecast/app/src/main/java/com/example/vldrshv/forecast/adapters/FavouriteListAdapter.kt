package com.example.vldrshv.forecast.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vldrshv.forecast.R

class FavouriteListAdapter : RecyclerView.Adapter<FavouriteListAdapter.FavouriteHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.favourite_location_card, parent, false)
        return FavouriteHolder(itemView)
    }

    override fun getItemCount(): Int {
        return 3//TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: FavouriteHolder, position: Int) {

    }


    class FavouriteHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {

        }
    }
}