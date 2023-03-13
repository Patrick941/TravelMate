package com.example.mapstemplate.ui.contacts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.mapstemplate.MainActivity
import com.example.mapstemplate.R
import com.example.mapstemplate.ui.current_user_itineraries.CurrentUserItinerariesFragment
import com.google.firebase.auth.FirebaseAuth

class AddFriend : AppCompatActivity() {

    private lateinit var addButton : Button
    private lateinit var email : TextView

    var tempString : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MyTag", "Program Created")
        setContentView(R.layout.activity_add_friend)

        addButton = findViewById(R.id.SignUpButton)
        email = findViewById(R.id.MessageText)

        addButton.setOnClickListener{
            Log.i("FriendsTag", "Adding new friend with email ${email.text}")
            tempString = email.text as String?
            //val intent = Intent(this, MainActivity::class.java)
            //intent.putExtra("email", email.text)
            //val contactsFragment = ContactsFragment()
            //val fragmentTransaction = fragmentManager.beginTransaction()
            //val fragmentManager = supportFragmentManager
            //fragmentTransaction.replace(R.id.nav_home, contactsFragment)
            //fragmentTransaction.commit()
            //startActivity(intent)
            finish()
        }


    }

}