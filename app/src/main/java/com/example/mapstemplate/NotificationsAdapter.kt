package com.example.mapstemplate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotificationsAdapter(private val context: HomeActivity) :
    RecyclerView.Adapter<NotificationsAdapter.UserViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType : Int):UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.notifications_box_recycler, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int){
        holder.textName.text = "Notification #$position"
    }

    override fun getItemCount(): Int{
        return 20
    }

    class UserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val textName: TextView = itemView.findViewById(R.id.notificationTV)
    }

}