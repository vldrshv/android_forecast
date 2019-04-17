package com.example.vldrshv.forecast.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vldrshv.forecast.Location
import com.example.vldrshv.forecast.R
import kotlinx.android.synthetic.main.current_locations_fragment.view.*
import kotlinx.android.synthetic.main.favourite_location_card.view.*

class LocationListAdapter(
        var locationList: ArrayList<Location>,
        var context: Context,
        var listener: (Location) -> Unit
) : RecyclerView.Adapter<LocationListAdapter.LocationHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.favourite_location_card, parent, false)
        return LocationHolder(itemView, locationList)
    }

    override fun getItemCount(): Int {
        return locationList.size
    }


    override fun onBindViewHolder(holder: LocationHolder, position: Int) {
        holder.bind(locationList[position], listener)
    }

    class LocationHolder(itemView: View, var locationList: List<Location>) : RecyclerView.ViewHolder(itemView) {
        fun bind(location: Location, listener: (Location) -> Unit) {
            println(location)
            if (location.isNull()){
                itemView.tvLocation.text = ""
                itemView.tvCountry.text = ""
                itemView.tvTemperature.text = ""
                itemView.tvTime.text = ""
                //itemView.setOnClickListener { listener(location) }
                return
            }
            if(locationList[0].isNull())
                itemView.imageWeather.visibility = View.INVISIBLE
            itemView.tvLocation.text = if (location.cityEng.equals("")) location.localizedName else location.cityEng
            itemView.tvCountry.text = location.country.name
            itemView.layoutWeather.tvTemperature.text = location.id.toString()
            itemView.setOnClickListener { listener(location) }

        }
    }
}