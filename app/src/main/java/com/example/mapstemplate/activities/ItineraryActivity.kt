package com.example.mapstemplate.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.mapstemplate.HomeActivity
import com.example.mapstemplate.R
import com.example.travelapp.adapters.StepListAdapter
import com.example.travelapp.itineraries.Itinerary
import com.google.firebase.firestore.FirebaseFirestore

class ItineraryActivity : AppCompatActivity() {
    lateinit var textViewTitle : TextView
    lateinit var listViewSteps: ListView
    lateinit var addButton: ImageView
    lateinit var backArrow: ImageView
    lateinit var stepListAdapter: StepListAdapter
    lateinit var buttonImageActivity: Button
    lateinit var itinerary: Itinerary
    lateinit var deleteItineraryButton: ImageView

    var itineraryIndex: Int = 0
    var isGlobal: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itinerary)

        // get extras
        itineraryIndex = intent.getIntExtra("itinerary_index", 0)
        isGlobal = intent.getBooleanExtra("is_global", true)


        if (isGlobal)
            itinerary = HomeActivity.globalItineraryList[itineraryIndex]
        else
            itinerary = HomeActivity.currentUserItineraryList[itineraryIndex]

        textViewTitle = findViewById(R.id.textView_title)
        listViewSteps = findViewById(R.id.listView_steps)
        addButton = findViewById(R.id.button_add_step)
        backArrow = findViewById(R.id.back_arrow_itinerary_activity)
        buttonImageActivity = findViewById(R.id.button_image_activity)
        deleteItineraryButton = findViewById(R.id.button_delete_itinerary)

        // hide add icon if is global
        if (isGlobal)
            addButton.isVisible = false

        textViewTitle.text = itinerary.name
        setupListView(itinerary)
        setupButtons()
    }

    fun setupListView(itinerary: Itinerary) {
        stepListAdapter = StepListAdapter(this, itinerary.steps)
        listViewSteps.isClickable = true
        listViewSteps.adapter = stepListAdapter

        listViewSteps.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, StepViewActivity::class.java)
            intent.putExtra("itinerary_index", itineraryIndex)
            intent.putExtra("step_index", position)
            startActivity(intent)
        }
    }

    fun setupButtons() {
        if (!isGlobal) {
            addButton.setOnClickListener {
                val intent = Intent(this, AddStepActivity::class.java)
                intent.putExtra("itinerary_index", itineraryIndex)
                startActivity(intent)
            }
        }

        backArrow.setOnClickListener {
            finish()
        }

        buttonImageActivity.setOnClickListener {
            val intent = Intent(this, ImagesItineraryVisualisationActivity::class.java)
            intent.putExtra("itinerary_name", itinerary.name)
            startActivity(intent)
        }

        deleteItineraryButton.setOnClickListener {
            warningDeletePopup()
        }
    }

    fun warningDeletePopup() {
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setMessage("Are you sure you want to delete this itinerary ?")
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.dismiss()
        })
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Delete", DialogInterface.OnClickListener { dialogInterface, i ->
            deleteItineraryFromFirebase()
            dialogInterface.dismiss()
        })
        alertDialog.show()
    }
    fun deleteItineraryFromFirebase() {
        val db = FirebaseFirestore.getInstance()
        val itinerary = HomeActivity.currentUserItineraryList[itineraryIndex]
        val itineraryId = itinerary.id
        val TAG = "ItineraryActivity"

        // Delete all steps of the itinerary from Firestore
        for (step in itinerary.steps) {
            db.collection("itineraries")
                .document(itineraryId)
                .collection("steps")
                .document(step.id)
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "All steps successfully deleted!")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error deleting steps", e)
                }
        }

        // Delete the itinerary from Firestore
        db.collection("itineraries")
            .document(itineraryId)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Itinerary successfully deleted!")
                // Now remove it from the local data structure
                HomeActivity.currentUserItineraryList.removeAt(itineraryIndex)
                stepListAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting itinerary", e)
            }
    }


    override fun onResume() {
        super.onResume()
        // update the data in the listView
        stepListAdapter.notifyDataSetChanged()
    }
}
