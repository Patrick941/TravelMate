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

        email.setOnFocusChangeListener { _, hasFocus ->
            addButton.visibility = if (hasFocus) Button.VISIBLE else Button.GONE
        }

        mAuth = FirebaseAuth.getInstance()

        mDbRef = FirebaseDatabase.getInstance().reference

        mDbRef.child("user").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //friendsList.clear()
                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)

                    if(mAuth.currentUser?.uid == currentUser?.uid) {
                        currentUser?.nick = "you"
                    }
                    friendsList.add(currentUser!!)
                    currentUser.nick?.let { friendsNames.add(it) }
                    Log.i("MyTag", "Adding user with email ${currentUser.email} to contacts")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        contactsAdapter = ContactsAdapter(actualFriendsNames)
        contactsRecycler = findViewById(R.id.friendsRecycler)
        contactsRecycler.layoutManager = LinearLayoutManager(this@AddFriend)
        contactsRecycler.adapter = contactsAdapter

        addButton.setOnClickListener{
            //Log.i("FriendsTag", "Adding new friend with email ${email.text}")

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
