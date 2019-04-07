package com.example.vldrshv.forecast.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vldrshv.forecast.Location
import com.example.vldrshv.forecast.R
import kotlinx.android.synthetic.main.favourite_location_card.view.*

class LocationListAdapter(var locationList : List<Location>, var context: Context)
    : RecyclerView.Adapter<LocationListAdapter.LocationHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.favourite_location_card, parent, false)
        return LocationHolder(itemView)
    }

    override fun getItemCount(): Int {
        return locationList.size
    }

    override fun onBindViewHolder(holder: LocationHolder, position: Int) {
        holder?.layoutWeather.visibility = View.GONE
        holder?.imageWeather.setImageResource(R.drawable.night_clear)
        holder?.tvLocation.text = if (locationList[position].cityEng.equals(""))
            locationList[position].cityRus
        else
            locationList[position].cityEng
        holder?.tvCountry.text = locationList[position].country.name
    }


    class LocationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageWeather = itemView.imageWeather
            val tvLocation = itemView.tvLocation
            val tvCountry = itemView.tvCountry
            val layoutWeather = itemView.layoutWeather
    }
}