package com.example.mapstemplate.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.mapstemplate.R
import com.example.travelapp.itineraries.Step
import com.example.mapstemplate.ui.itinerary.ItineraryFragment.Companion.itineraryList

class CreateItemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_item)
        val submitButton = findViewById<Button>(R.id.submit_button)

        val itineraryIndex:Int = intent.getIntExtra("itinerary_index", 0)

        submitButton.setOnClickListener{
            val name = findViewById<EditText>(R.id.item_title).text.toString()
            val address = findViewById<EditText>(R.id.item_location).text.toString()
            val description = findViewById<EditText>(R.id.item_description).text.toString()
            var cost = 0f
            try {
                cost = findViewById<EditText>(R.id.item_cost).text.toString().toFloat()
            } catch (e: Exception) {
                print("ERROR : " + e.message)
            }

            itineraryList[itineraryIndex].steps.add(Step(name, address, cost, description))
            finish()
        }
    }
}
