package com.example.mapstemplate.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.mapstemplate.R
import com.example.mapstemplate.ui.itinerary.ItineraryFragment
import com.example.travelapp.itineraries.Itinerary

class AddItineraryActivity : AppCompatActivity() {
    lateinit var back_arrow: ImageView
    lateinit var editTextTitle: EditText
    lateinit var addButton: Button

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
            val newItinerary = Itinerary(editTextTitle.text.toString())
            // statement to replace blank names, not needed but *aesthetics*
            if (newItinerary.name == "") newItinerary. name = "Itinerary"
            ItineraryFragment.itineraryList.add(newItinerary)

            // Return to previous page
            finish()
        }
    }
}