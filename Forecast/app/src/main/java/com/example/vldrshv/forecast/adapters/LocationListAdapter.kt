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


/**
 * LOCATION LIST ADAPTER
 *
 * Uses to make RecycleView contain LocationView
 * Contains of:
 * --   locationList - List of locations
 * --   context - current activity context)
 * --   listener - onItemClickListener
 *
 * One class for SEARCH LOCATION FRAGMENT and FAVOURITE LOCATIONS FRAGMENT
 * todo #1 add weather update (weather temp)
 * todo #2 add weather update (weather icon)
 * todo #3 add time stamp (localization)
 */
class LocationListAdapter(
        var locationList: ArrayList<Location>,
        var context: Context,
        var listener: (Location) -> Unit
) : RecyclerView.Adapter<LocationListAdapter.LocationHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.favourite_location_card, parent, false)
        return LocationHolder(itemView)
    }

    override fun getItemCount(): Int {
        return locationList.size
    }


    override fun onBindViewHolder(holder: LocationHolder, position: Int) {
        holder.bind(locationList[position], listener)
    }

    class LocationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(location: Location, listener: (Location) -> Unit) {

            if (location.isNull()){
                itemView.tvLocation.text = ""
                itemView.tvCountry.text = ""
                itemView.tvTemperature.text = ""
                itemView.tvTime.text = ""
                return
            }

            itemView.tvLocation.text = if (location.cityEng.equals("")) location.localizedName else location.cityEng
            itemView.tvCountry.text = location.country.name
            itemView.layoutWeather.tvTemperature.text = location.id.toString()
            itemView.setOnClickListener { listener(location) }

        }
    }
}