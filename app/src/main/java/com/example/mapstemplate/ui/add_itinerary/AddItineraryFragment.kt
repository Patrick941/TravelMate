package com.example.mapstemplate.ui.add_itinerary

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.mapstemplate.HomeActivity
import com.example.mapstemplate.R
import com.example.mapstemplate.activities.ItineraryActivity
import com.example.travelapp.itineraries.Itinerary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AddItineraryFragment : Fragment() {
    lateinit var addButton: Button
    lateinit var editTextTitle: EditText

    private val mAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflater.inflate(R.layout.fragment_add_itinerary, container, false)
        val view = binding.rootView

        addButton = view.findViewById(R.id.button_add_itinerary)
        editTextTitle = view.findViewById(R.id.editText_add_itinerary_title)

        setupAddButton()

        // Inflate the layout for this fragment
        return binding
    }

    /**
     * Call addItinerary function if the Title is usable
     */
    fun setupAddButton() {
        addButton.setOnClickListener {
            if (editTextTitle.text.toString().equals("")) {
                Toast.makeText(context, "You need to enter an itinerary name", Toast.LENGTH_SHORT).show()
                editTextTitle.setHintTextColor(Color.RED)
            } else {
                editTextTitle.setHintTextColor(Color.BLACK)
                addItinerary(editTextTitle.text.toString())
                editTextTitle.setText("")
            }
        }
    }

    /**
     * Create a new itinerary and open it in a new activity
     */
    fun addItinerary(title: String) {
        val itineraryHashMap = hashMapOf(
            "title" to title,
            "user_email" to mAuth.currentUser!!.email
        )

        // Add the itinerary in firestore
        db.collection("itineraries")
            .add(itineraryHashMap)
            .addOnSuccessListener { documentReference ->
                // Add the itinerary in the currentUserItineraryList
                val itinerary = Itinerary(title, documentReference.id)
                HomeActivity.currentUserItineraryList.add(itinerary)

                // Get index of the itinerary in the list
                val position: Int = HomeActivity.currentUserItineraryList.indexOf(itinerary)

                // Start the Itinerary activity with the created itinerary
                val intent = Intent(context, ItineraryActivity::class.java)
                intent.putExtra("itinerary_index", position)
                intent.putExtra("is_global", false)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.w("DEBUG", "Error adding document", e)
            }
    }
}