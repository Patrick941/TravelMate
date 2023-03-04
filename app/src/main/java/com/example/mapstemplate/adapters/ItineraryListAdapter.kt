package com.example.travelapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.mapstemplate.R
import com.example.travelapp.itineraries.Itinerary

class ItineraryListAdapter(private val context: Context, dataArray: List<Itinerary>) : ArrayAdapter<Itinerary>(context,
    R.layout.itinerary_list_view, dataArray) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itinerary = getItem(position)
        val view : View = LayoutInflater.from(context).inflate(R.layout.itinerary_list_view, null)

        val title = view.findViewById<TextView>(R.id.textView_itinerary_title)
        val price = view.findViewById<TextView>(R.id.textView_itinerary_price)

        title.text = itinerary!!.name
        price.text =  "${itinerary.calculateItineraryPrice()} €"
        return view
    }
}