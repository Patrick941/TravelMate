package com.example.mapstemplate.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ListView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.mapstemplate.HomeActivity
import com.example.mapstemplate.R
import com.example.mapstemplate.databinding.ActivityHomeBinding
import com.example.travelapp.adapters.StepListAdapter
import com.example.travelapp.itineraries.Itinerary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ItineraryActivity : AppCompatActivity() {
    lateinit var textViewTitle : TextView
    lateinit var listViewSteps: ListView
    lateinit var addButton: ImageView
    lateinit var backArrow: ImageView
    lateinit var stepListAdapter: StepListAdapter
    lateinit var ratingBar: RatingBar

    lateinit var itinerary: Itinerary
    // private lateinit var binding: ActivityHomeBinding
    var itineraryIndex: Int = 0
    var isGlobal: Boolean = true
    var originRating: Float = 0f;

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

        // hide add icon if is global
        if (isGlobal)
            addButton.isVisible = false

        textViewTitle.text = itinerary.name

        setupListView(itinerary)
        setupButtons()
        setupRatingBar()
    }

    fun setupRatingBar() {
        originRating = fetchUserRate()
        ratingBar.rating = originRating

        ratingBar.setOnRatingBarChangeListener { ratingBar, rate, b ->
            Log.d("DEBUG", "setupRatingBar: ${ratingBar}, ${rate}, ${b}")
            updateRatingBar(rate)
        }
    }

    /**
     * Get the last stored value of the rate that the current user give for a specific itinerary, or 0
     */
    private fun fetchUserRate(): Float {
        // tmp value waiting for group-itinerary-benoit branch merging
        val itineraryId = "TMP_ID"
        val docItineraryUserRateRef = db.collection("user/${mAuth.uid}/itineraryRates")
        val rateDocs = docItineraryUserRateRef.get().result
        for (doc in rateDocs) {
            if (doc.id == itineraryId && doc.data.containsValue("rate"))
                return doc.data.get("rate") as Float
        }

        return 0f;
    }

    /**
     * Update the rating of the itinerary in firestore and update user rates in firestore
     */
    private fun updateRatingBar(rate: Float) {
        // tmp value waiting for group-itinerary-benoit branch merging
        val itineraryId = "TMP_ID"
        val originalRateNumber = 2

        val afterUpdateRating = (originalRateNumber * originRating + rate) / originalRateNumber+1

        // update fields in a specific itinerary
        val docItineraryRef = db.collection("itineraries").document(itineraryId)
        docItineraryRef.update("rating", afterUpdateRating, "number_of_rates", originalRateNumber+1)

        // update his profile information with this change
        val userRateHashMap = hashMapOf(
            "rate" to rate
        )
        val docItineraryUserRateRef = db.collection("user/${mAuth.uid}/itineraryRates").document(itineraryId)
        docItineraryUserRateRef.set(userRateHashMap)

        originRating = afterUpdateRating
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
    }

    override fun onResume() {
        super.onResume()
        // update the data in the listView
        stepListAdapter.notifyDataSetChanged()
    }
}