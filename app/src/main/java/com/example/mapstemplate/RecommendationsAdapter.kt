package com.example.mapstemplate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecommendationsAdapter() :
    RecyclerView.Adapter<RecommendationsAdapter.UserViewHolder>(){

    // Assigned the recommendations_box_recycler to act as the view, it will be updated, currently just
    // displays text
    override fun onCreateViewHolder(parent: ViewGroup, viewType : Int):UserViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.recommendations_box_recycler, parent, false)
        return UserViewHolder(view)
    }

    // Placeholder text to be assigned to the textName object of the view
    override fun onBindViewHolder(holder: UserViewHolder, position: Int){
        holder.textName.text = "Recommendation #$position"
    }

    // Temporary number, must be changed, may cause crashes and segmentation faults when other
    // aspects of code are changed
    override fun getItemCount(): Int{
        return 15
    }

    // Objects from view assigned to vals
    class UserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val textName: TextView = itemView.findViewById(R.id.recommendationTV)
    }

}