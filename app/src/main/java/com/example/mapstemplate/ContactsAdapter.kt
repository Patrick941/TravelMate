package com.example.mapstemplate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler

class ContactsAdapter() :
    RecyclerView.Adapter<ContactsAdapter.UserViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType : Int):UserViewHolder {
        //Simple view created with the notifications_box_recycler used as a temporary placeholder, will be replaced
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.notifications_box_recycler, parent, false)
        return UserViewHolder(view)
    }

    //Placeholder text to be replaced
    override fun onBindViewHolder(holder: UserViewHolder, position: Int){
        holder.textName.text = "Contact #$position"
    }

    //Placeholder number to be changed (should not be a constant, should be size of list)
    //Represents the amount of views in recycler
    override fun getItemCount(): Int{
        return 20
    }

    //View items to be attached to each view
    class UserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val textName: TextView = itemView.findViewById(R.id.notificationTV)
    }

}