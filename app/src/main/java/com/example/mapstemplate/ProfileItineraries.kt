package com.example.mapstemplate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.mapstemplate.activities.ItineraryActivity
import com.example.travelapp.adapters.ItineraryListAdapter
import com.example.travelapp.itineraries.Itinerary
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.atomic.AtomicInteger


class ProfileItineraries : AppCompatActivity() {

    lateinit var listViewItinerary: ListView
    lateinit var itineraryListAdapter: ItineraryListAdapter

    private val itineraryList: ArrayList<Itinerary> = ArrayList()

    lateinit var friend: User
    private val filteredItineraries = ArrayList<Itinerary>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_itineraries)

        itineraryList.addAll(HomeActivity.globalItineraryList)

        listViewItinerary = findViewById(R.id.global_itineraries_list_item)

        val friendJson = intent.getStringExtra("friend")
        if (friendJson != null) {
            try {
                friend = Gson().fromJson<User>(friendJson, User::class.java)
                val titleTextView = findViewById<TextView>(R.id.title)
                titleTextView.text = friend.nick
            } catch (e: Exception) {
                Log.i("ProfilesTag", "Error deserializing friend: ${e.message}")
            }
        }
        else {
            // Handle the case where the friendJson variable is null or empty
            Toast.makeText(this, "No friend data found", Toast.LENGTH_SHORT).show()
        }





        setupItineraryListView()
        logItineraryEmails()

        val searchButton = findViewById<Button>(R.id.btnSearch)
        val inputField = findViewById<EditText>(R.id.itineraryName)
        var enteredSearch = ""

        searchButton.setOnClickListener {
            val newItineraryList = ArrayList<Itinerary>()
            enteredSearch = inputField.text.toString()
            for (i in HomeActivity.globalItineraryList) {
                if (i.name.contains(enteredSearch, true)) {
                    newItineraryList.add(i)
                } else {
                    Toast.makeText(
                        this,
                        "Please, enter an itinerary name",
                        Toast.LENGTH_SHORT).show()
                }
            }
            itineraryList.clear()
            itineraryList.addAll(newItineraryList)
            itineraryListAdapter.notifyDataSetChanged()

        }
    }

    private fun logItineraryEmails() {
        val db = FirebaseFirestore.getInstance()

        Log.i("ProfileItineraries", "Beginning logging for ${friend.email}")
        val pendingRequests = AtomicInteger(itineraryList.size)
        for (itinerary in itineraryList) {
            Log.i("ProfileItineraries", "Checking new itinerary")
            db.collection("itineraries")
                .document(itinerary.itineraryId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userEmail = document.getString("user_email") ?: "No email found"
                        Log.i("ProfileItineraries", "Itinerary ID: ${itinerary.itineraryId}, User Email: $userEmail")

                        if (userEmail == friend.email) {
                            filteredItineraries.add(itinerary)
                            Log.i("ProfileItineraries", "Added itinerary to the list as emails match")
                        }
                    } else {
                        Log.i("ProfileItineraries", "No such document for Itinerary ID: ${itinerary.itineraryId}")
                    }

                    if (pendingRequests.decrementAndGet() == 0) {
                        setupItineraryListView()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.i("ProfileItineraries", "Error getting documents.", exception)

                    if (pendingRequests.decrementAndGet() == 0) {
                        setupItineraryListView()
                    }
                }
        }
    }



    fun setupItineraryListView() {
        itineraryListAdapter = ItineraryListAdapter(this@ProfileItineraries, filteredItineraries) { position ->
            val intent = Intent(this@ProfileItineraries, ItineraryActivity::class.java)
            intent.putExtra("itinerary_index", position)
            intent.putExtra("is_global", true)
            startActivity(intent)
        }

        listViewItinerary.isClickable = true
        listViewItinerary.adapter = itineraryListAdapter
    }
    override fun onResume() {
        super.onResume()
        // update the data in the listView
        itineraryListAdapter.notifyDataSetChanged()
    }
}
