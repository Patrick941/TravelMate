package com.example.mapstemplate.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.mapstemplate.HomeActivity
import com.example.mapstemplate.R
import com.example.travelapp.adapters.StepListAdapter
import com.example.travelapp.itineraries.Itinerary

class ItineraryActivity : AppCompatActivity() {
    lateinit var textViewTitle : TextView
    lateinit var listViewSteps: ListView
    lateinit var addButton: ImageView
    lateinit var backArrow: ImageView
    lateinit var stepListAdapter: StepListAdapter
    var itineraryIndex: Int = 0
    var isGlobal: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itinerary)

        // get extras
        itineraryIndex = intent.getIntExtra("itinerary_index", 0)
        isGlobal = intent.getBooleanExtra("is_global", true)

        var itinerary: Itinerary
        if (isGlobal)
            itinerary = HomeActivity.globalItineraryList[itineraryIndex]
        else
            itinerary = HomeActivity.currentUserItineraryList[itineraryIndex]

        textViewTitle = findViewById(R.id.textView_title)
        listViewSteps = findViewById(R.id.listView_steps)
        addButton = findViewById(R.id.button_add_step)
        backArrow = findViewById(R.id.back_arrow_itinerary_activity)

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
    }

    override fun onResume() {
        super.onResume()
        // update the data in the listView
        stepListAdapter.notifyDataSetChanged()
    }
}