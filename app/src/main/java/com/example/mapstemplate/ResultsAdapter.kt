package com.example.mapstemplate

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng

class ResultsAdapter(private val locations: ArrayList<String>,private val cordinates: ArrayList<LatLng>) :
    RecyclerView.Adapter<ResultsAdapter.UserViewHolder>(){

    // Assigned the notifications_box_recycler to act as the view, it will be updated, currently just
    // displays text
    override fun onCreateViewHolder(parent: ViewGroup, viewType : Int):UserViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.notifications_box_recycler, parent, false)
        return UserViewHolder(view)
    }

    // Placeholder text to be assigned to the textName object of the view
    override fun onBindViewHolder(holder: ResultsAdapter.UserViewHolder, position: Int){
        Log.i("PlacesAPI", "Adding Item to recommendations")
        if(position < locations.size) {
            holder.textName.text = "${locations[position]}"
        } else {
            holder.textName.text = "Placeholder"
        }
        holder.cardView.setOnClickListener{
            val intent = Intent(holder.itemView.context, MapsActivity::class.java)
            intent.putExtra("lat", cordinates[position].latitude)
            intent.putExtra("lng", cordinates[position].longitude)
            intent.putExtra("mode", 1)
            holder.itemView.context.startActivity(intent)
        }
    }

    // Temporary number, must be changed, may cause crashes and segmentation faults when other
    // aspects of code are changed
    override fun getItemCount(): Int{
        return locations.size
    }

    // Objects from view assigned to vals
    class UserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val textName: TextView = itemView.findViewById(R.id.source)
        val cardView: CardView = itemView.findViewById(R.id.card_view)
    }

}