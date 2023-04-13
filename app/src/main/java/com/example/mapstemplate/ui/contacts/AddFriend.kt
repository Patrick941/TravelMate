package com.example.mapstemplate.ui.contacts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapstemplate.ContactsAdapter
import com.example.mapstemplate.MainActivity
import com.example.mapstemplate.R
import com.example.mapstemplate.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AddFriend : AppCompatActivity() {

    private lateinit var addButton : Button
    private lateinit var email : TextView

    var tempString : String? = null

    private val thisName = "AddFriend"

    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth : FirebaseAuth

    private lateinit var friendsList : ArrayList<User>
    private lateinit var friendsNames : ArrayList<String>

    private lateinit var contactsRecycler : RecyclerView
    private lateinit var contactsAdapter : ContactsAdapter

    private lateinit var actualFriends : ArrayList<User>
    private lateinit var actualFriendsNames : ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MyTag", "Program Created")
        setContentView(R.layout.activity_add_friend)

        friendsList = ArrayList()
        friendsNames = ArrayList()
        actualFriendsNames = ArrayList()
        actualFriends = ArrayList()

        addButton = findViewById(R.id.addButton)
        email = findViewById(R.id.editText)

        val userId = mAuth.currentUser?.uid

        email.setOnFocusChangeListener { _, hasFocus ->
            addButton.visibility = if (hasFocus) Button.VISIBLE else Button.GONE
        }

        mAuth = FirebaseAuth.getInstance()

        mDbRef = FirebaseDatabase.getInstance().reference

        mDbRef.child("user").child(userId!!).child("Friends").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (friendSnapshot in snapshot.children) {
                    val friendEmail = friendSnapshot.getValue(String::class.java)
                    friendsNames.add(friendEmail!!)
                    Log.i("MyTag", "Adding friend with email $friendEmail to contacts")
                }
                contactsAdapter.notifyDataSetChanged() // Notify the adapter that the data has changed
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("MyTag", "Failed to read value.", error.toException())
            }
        })


        contactsAdapter = ContactsAdapter(actualFriendsNames)
        contactsRecycler = findViewById(R.id.friendsRecycler)
        contactsRecycler.layoutManager = LinearLayoutManager(this@AddFriend)
        contactsRecycler.adapter = contactsAdapter

        addButton.setOnClickListener{ // Get the current user ID
            val friendEmail = email.text.toString()  // Replace with the email you want to add

            val friendRef = mDbRef.child("user").child(userId!!).child("Friends").push()
            friendRef.setValue(friendEmail)


            finish()
        }
    }

    //logging activity changes
    override fun onPause(){
        super.onPause()
        Log.i("MyTag", "pausing $thisName")
    }
    override fun onResume(){
        super.onResume()
        Log.i("MyTag", "resuming $thisName")
    }
    override fun onStart(){
        super.onStart()
        Log.i("MyTag", "starting $thisName")
    }
    override fun onStop(){
        super.onStop()
        Log.i("MyTag", "stopping $thisName")
    }
    override fun onDestroy(){
        super.onDestroy()
        Log.i("MyTag", "destroying $thisName")
    }
}
