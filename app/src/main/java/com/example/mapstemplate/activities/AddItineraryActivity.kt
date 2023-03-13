package com.example.mapstemplate.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.mapstemplate.HomeActivity
import com.example.mapstemplate.R
import com.example.mapstemplate.ui.itinerary.ItineraryFragment
import com.example.travelapp.itineraries.Itinerary
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddItineraryActivity : AppCompatActivity() {
    lateinit var back_arrow: ImageView
    lateinit var editTextTitle: EditText
    lateinit var addButton: Button

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_itinerary)

        back_arrow = findViewById(R.id.back_arrow_add_itinerary_activity)
        editTextTitle = findViewById(R.id.editText_add_itinerary_title)
        addButton = findViewById(R.id.button_add_itinerary)

        setupButtons()
    }

    fun setupButtons() {
        back_arrow.setOnClickListener {
            finish()
        }

        addButton.setOnClickListener {
            var name = editTextTitle.text.toString()
            // statement to replace blank names, not needed but *aesthetics*
            if (name == "")
                name = "Itinerary"

            addItinerary(name)

            // Return to previous page
            finish()
        }
    }

    fun addItinerary(title: String) {
        val itineraryHashMap = hashMapOf(
            "title" to title
        )

        db.collection("itineraries")
            .add(itineraryHashMap)
            .addOnSuccessListener { documentReference ->
                val itinerary = Itinerary(title, documentReference.id)
                HomeActivity.userItineraryList.add(itinerary)
            }
            .addOnFailureListener { e ->
                Log.w("DEBUG", "Error adding document", e)
            }
    }
}