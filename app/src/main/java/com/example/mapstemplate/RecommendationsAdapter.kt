package com.example.mapstemplate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecommendationsAdapter(private val context: HomeActivity) :
    RecyclerView.Adapter<RecommendationsAdapter.UserViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType : Int):UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.recommendations_box_recycler, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int){
        holder.textName.text = "Recommendation #$position"
    }

    override fun getItemCount(): Int{
        return 15
    }

    class UserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val textName: TextView = itemView.findViewById(R.id.recommendationTV)
    }

}