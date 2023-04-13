package com.example.mapstemplate

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class ContactsAdapter(private val friends: ArrayList<User>) :
    RecyclerView.Adapter<ContactsAdapter.UserViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType : Int):UserViewHolder {
        //Simple view created with the notifications_box_recycler used as a temporary placeholder, will be replaced
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.notifications_box_recycler, parent, false)
        return UserViewHolder(view)
    }

    //Placeholder text to be replaced
    override fun onBindViewHolder(holder: UserViewHolder, position: Int){
        if (position < friends.size) {
            holder.textName.text = friends[position].nick
        } else {
            holder.textName.text = "Error in contacts list length"
        }
        holder.cardView.setOnClickListener{
            val intent = Intent(holder.itemView.context, ProfileItineraries::class.java)
            val friend = friends[position]
            val serializedFriend = Gson().toJson(friend)
            Log.i("ProfilesTag", "Serialized friend: $serializedFriend")
            intent.putExtra("friend", serializedFriend)
            holder.itemView.context.startActivity(intent)
        }



    }

    //Placeholder number to be changed (should not be a constant, should be size of list)
    //Represents the amount of views in recycler
    override fun getItemCount(): Int{
        return friends.size
    }

    //View items to be attached to each view
    class UserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val textName: TextView = itemView.findViewById(R.id.source)
        val cardView: CardView = itemView.findViewById(R.id.card_view)
    }

}