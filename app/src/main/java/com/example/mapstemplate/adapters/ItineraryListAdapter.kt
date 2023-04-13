package com.example.travelapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.mapstemplate.R
import com.example.travelapp.itineraries.Itinerary
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage

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

        val storageReference = FirebaseStorage.getInstance().getReference("images_itineraries/itinerary id/main_image")

        storageReference.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(context)
                .load(uri)
                .into(imageView)
        }.addOnFailureListener {
            // Handle any errors
        }

        return view
    }
}
