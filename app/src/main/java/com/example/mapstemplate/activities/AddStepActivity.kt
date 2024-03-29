package com.example.mapstemplate.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import com.example.mapstemplate.HomeActivity
import com.example.mapstemplate.R
import com.example.mapstemplate.adapter.PlaceAutoSuggestAdapter
import com.example.travelapp.itineraries.Itinerary
import com.example.travelapp.itineraries.Step
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddStepActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_item)
        val submitButton = findViewById<Button>(R.id.submit_button)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autocomplete)
        autoCompleteTextView.setAdapter(PlaceAutoSuggestAdapter(this, android.R.layout.simple_list_item_1))

        val itineraryIndex:Int = intent.getIntExtra("itinerary_index", 0)

        submitButton.setOnClickListener{
            val name = findViewById<EditText>(R.id.item_title).text.toString()
            val address = findViewById<EditText>(R.id.autocomplete).text.toString()
            val description = findViewById<EditText>(R.id.item_description).text.toString()
            var cost = 0.0
            try {
                cost = findViewById<EditText>(R.id.item_cost).text.toString().toDouble()
            } catch (e: Exception) {
                print("ERROR : " + e.message)
            }

            addStep(HomeActivity.currentUserItineraryList[itineraryIndex], name, address, cost, description)
        }

    }

    fun addStep(itinerary: Itinerary, name: String, address: String, price: Double, description: String) {
        val itineraryHashMap = hashMapOf(
            "name" to name,
            "address" to address,
            "price" to price,
            "description" to description
        )

        db.collection("itineraries/${itinerary.itineraryId}/steps")
            .add(itineraryHashMap)
            .addOnSuccessListener { documentReference ->
                val step = Step(documentReference.id, name, address, price, description)
                itinerary.steps.add(step)
                finish()
            }
            .addOnFailureListener { e ->
                Log.w("DEBUG", "Error adding document", e)
                finish()
            }
    }
}
