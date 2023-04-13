package com.example.travelapp.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.mapstemplate.R
import com.example.travelapp.itineraries.Itinerary
import com.example.mapstemplate.HomeActivity
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ItineraryListAdapter(private val context: Context, dataArray: List<Itinerary>) : ArrayAdapter<Itinerary>(context,
    R.layout.itinerary_list_view, dataArray) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itinerary = getItem(position)
        val view: View = LayoutInflater.from(context).inflate(R.layout.itinerary_list_view, null)

        val imageView = view.findViewById<ImageView>(R.id.imageView_photo)
        val title = view.findViewById<TextView>(R.id.textView_itinerary_title)
        val price = view.findViewById<TextView>(R.id.textView_itinerary_price)

        title.text = itinerary!!.name
        price.text = "${itinerary.calculateItineraryPrice()} €"

        if (HomeActivity.mainImageItineraryMap.containsKey(itinerary.itineraryId)) {
            val localfile: File = HomeActivity.mainImageItineraryMap.get(itinerary.itineraryId)!!
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            imageView.setImageBitmap(bitmap)
        }
        imageView.clipToOutline = true
        return view
    }
}
