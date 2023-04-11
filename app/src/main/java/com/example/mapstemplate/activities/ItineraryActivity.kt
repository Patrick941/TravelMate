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
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.mapstemplate.HomeActivity
import com.example.mapstemplate.R
import com.example.mapstemplate.itineraries.UserRate
import com.example.travelapp.adapters.StepListAdapter
import com.example.travelapp.itineraries.Itinerary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore

class ItineraryActivity : AppCompatActivity() {
    lateinit var textViewTitle : TextView
    lateinit var listViewSteps: ListView
    lateinit var addButton: ImageView
    lateinit var backArrow: ImageView
    lateinit var stepListAdapter: StepListAdapter
    lateinit var ratingBar: RatingBar

    lateinit var buttonImageActivity: Button
    lateinit var itinerary: Itinerary
    lateinit var deleteItineraryButton: ImageView

    // private lateinit var binding: ActivityHomeBinding
    var itineraryIndex: Int = 0
    var isGlobal: Boolean = true
    lateinit var userRate: UserRate;
    var userHasRate: Boolean = false

    private val db = Firebase.firestore
    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itinerary)
        // setContentView(R.layout.activity_itinerary_constraint)
        // binding = ActivityHomeBinding.inflate(layoutInflater)
        // setSupportActionBar(binding.appBarHome.toolbar)

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
        ratingBar = findViewById(R.id.itinerary_rating_bar)
        buttonImageActivity = findViewById(R.id.button_image_activity)
        deleteItineraryButton = findViewById(R.id.button_delete_itinerary)

        // hide add icon if is global
        if (isGlobal)
            addButton.isVisible = false

        textViewTitle.text = itinerary.name

        setupListView(itinerary)
        setupButtons()
        setupRatingBar()
    }

    fun setupRatingBar() {
        getUserRate()
        ratingBar.rating = userRate.rate
        Log.d("DEBUG", "User rate : ${userRate.rate}")
        Log.d("DEBUG", "Rate : ${itinerary.rating}")

        ratingBar.setOnRatingBarChangeListener { ratingBar, rate, b ->
            updateRatingBar(rate)
        }
    }

    /**
     * Get the last stored value of the rate that the current user give for a specific itinerary, or 0
     */
    private fun getUserRate() {
        for (userRateInList in HomeActivity.userRateList) {
            if (userRateInList.itineraryId.equals(itinerary.id)) {
                userHasRate = true
                userRate = userRateInList
                return
            }
        }

        userRate = UserRate(itinerary.id, 0f)
    }

    /**
     * Update the rating of the itinerary in firestore and update user rates in firestore
     */
    private fun updateRatingBar(rate: Float) {
        var numberOfRateAfterUpdate: Int = itinerary.numberOfRate
        var ratingAfterUpdate: Float

        if (userHasRate && itinerary.numberOfRate != 0) {
            ratingAfterUpdate =
                (itinerary.numberOfRate * itinerary.rating + rate - userRate.rate) / itinerary.numberOfRate
        } else {
            HomeActivity.userRateList.add(userRate)
            userHasRate = true
            numberOfRateAfterUpdate = itinerary.numberOfRate + 1
            ratingAfterUpdate =
                (itinerary.numberOfRate * itinerary.rating + rate) / numberOfRateAfterUpdate
        }

        Log.d("DEBUG", "After update : ${ratingAfterUpdate}")

        // update fields in a specific itinerary
        val docItineraryRef = db.collection("itineraries").document(itinerary.id)
        docItineraryRef.update("rating", ratingAfterUpdate, "number_of_rates", numberOfRateAfterUpdate)


        // update his profile information with this change
        val userRateHashMap = hashMapOf(
            "rate" to rate
        )
        val docItineraryUserRateRef = db.collection("user/${mAuth.uid}/itineraryRates").document(itinerary.id)
        docItineraryUserRateRef.set(userRateHashMap)

        userRate.rate = rate
        itinerary.rating = ratingAfterUpdate
        itinerary.numberOfRate = numberOfRateAfterUpdate
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
            finish()
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
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting itinerary", e)
            }


        // Now remove it from the local data structure
        HomeActivity.currentUserItineraryList.removeAt(itineraryIndex)
    }


    override fun onResume() {
        super.onResume()
        // update the data in the listView
        stepListAdapter.notifyDataSetChanged()
    }
}
