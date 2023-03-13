package com.example.mapstemplate

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(private val history1: ArrayList<String>, private val history2: ArrayList<String>) :
    RecyclerView.Adapter<HistoryAdapter.UserViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType : Int):UserViewHolder {
        //Simple view created with the notifications_box_recycler used as a temporary placeholder, will be replaced
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.history_box_recycler, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        Log.i("PlacesAPI", "Adding Item to recommendations")
        if(position < history1.size && position < history2.size) {
            holder.start.text = history1[position]
            holder.end.text = history2[position]
        } else {
            holder.end.text = "Placeholder"
        }
    }

    // Temporary number, must be changed, may cause crashes and segmentation faults when other
    // aspects of code are changed
    override fun getItemCount(): Int{
        if (history1.size < history2.size){
            return history1.size
        } else {
            return history2.size
        }

    }

    // Objects from view assigned to vals
    class UserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val start: TextView = itemView.findViewById(R.id.source)
        val end: TextView = itemView.findViewById(R.id.destination)
    }
}
