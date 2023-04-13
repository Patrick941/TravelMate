package com.example.mapstemplate

import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng

class RecommendationsAdapter(private val locations: ArrayList<String>, private val rating: ArrayList<Number>,
                             private val images: ArrayList<Bitmap>, private val vicinity: ArrayList<String>,
                             private val cordinates: ArrayList<LatLng>
) :
    RecyclerView.Adapter<RecommendationsAdapter.UserViewHolder>(){

    // Assigned the recommendations_box_recycler to act as the view, it will be updated, currently just
    // displays text
    override fun onCreateViewHolder(parent: ViewGroup, viewType : Int):UserViewHolder {
//        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.recommendations_box_recycler, parent, false)
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.recommendations_card_view, parent, false)
        return UserViewHolder(view)
    }

    // Placeholder text to be assigned to the textName object of the view
    override fun onBindViewHolder(holder: UserViewHolder, position: Int){
        Log.i("PlacesAPI", "Adding Item to recommendations")
        if(position < locations.size) {
            holder.textName.text = "${locations[position]}"
            holder.rating.text = "Rating:${rating[position]}"
            holder.address.text = "${vicinity[position]}"
            if (images.size > position && images[position] != null) {
                holder.image.setImageBitmap(images[position])
            } else {
                holder.image.setImageResource(R.drawable.baseline_add_to_photos_40)
            }
            holder.cardView.setOnClickListener{
                val intent = Intent(holder.itemView.context, MapsActivity::class.java)
                intent.putExtra("lat", cordinates[position].latitude)
                intent.putExtra("lng", cordinates[position].longitude)
                holder.itemView.context.startActivity(intent)
            }
        } else {
            holder.textName.text = "Placeholder"
        }
    }



    // Temporary number, must be changed, may cause crashes and segmentation faults when other
    // aspects of code are changed
    override fun getItemCount(): Int{
        return locations.size
    }

    // Objects from view assigned to vals
    class UserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        // These are inverted, not bothered changing
        val rating: TextView = itemView.findViewById(R.id.recommendationsTextView)
        val textName: TextView = itemView.findViewById(R.id.ratingTextView)
        val address: TextView = itemView.findViewById(R.id.address)
        val image : ImageView = itemView.findViewById(R.id.imageView)
        val cardView: CardView = itemView.findViewById(R.id.card_view)
    }

}