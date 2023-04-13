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
    private lateinit var friendsToPrint : ArrayList<String>

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
        friendsToPrint = ArrayList()

        addButton = findViewById(R.id.addButton)
        email = findViewById(R.id.editText)



        email.setOnFocusChangeListener { _, hasFocus ->
            addButton.visibility = if (hasFocus) Button.VISIBLE else Button.GONE
        }

        mAuth = FirebaseAuth.getInstance()

        mDbRef = FirebaseDatabase.getInstance().reference

        val userId = mAuth.currentUser?.uid



        mDbRef.child("user").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)

                    if(mAuth.currentUser?.uid == currentUser?.uid) {
                        currentUser?.nick = "you"
                    }
                    friendsList.add(currentUser!!)
                    currentUser.nick?.let { friendsNames.add(it) }
                    Log.i("MyTag", "Adding user with email ${currentUser.email} to contacts")
                }
                // Move the second query here, inside the onDataChange callback of the first query
                mDbRef.child("user").child(userId!!).child("Friends").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (friendSnapshot in snapshot.children) {
                            val friendEmail = friendSnapshot.getValue(String::class.java)
                            actualFriendsNames.add(friendEmail!!)
                            Log.i("MyTag", "Adding friend with email $friendEmail to contacts")

                            // Check if the email is in the friends list
                            for (friend in friendsList) {
                                Log.i("MyTag", "Testing for ${friend.nick}")
                                if (friend.email == friendEmail) {
                                    // Add the friend's nickname to a new array
                                    actualFriends.add(friend)
                                    Log.i("MyTag", "Adding friend with nickname ${friend.nick} to actualFriends")
                                    break
                                }
                            }
                        }
                        contactsAdapter.notifyDataSetChanged() // Notify the adapter that the data has changed
                        for (friend in actualFriends) {
                            friendsToPrint.add(friend.nick!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w("MyTag", "Failed to read value.", error.toException())
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })




        contactsAdapter = ContactsAdapter(friendsToPrint)
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
