package com.example.mapstemplate.ui.add_itinerary

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
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
import androidx.activity.result.contract.ActivityResultContracts
import com.example.mapstemplate.HomeActivity
import com.example.mapstemplate.R
import com.example.mapstemplate.activities.ItineraryActivity
import com.example.travelapp.itineraries.Itinerary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.*


class AddItineraryFragment : Fragment() {
    lateinit var addButton: Button
    lateinit var editTextTitle: EditText
    lateinit var searchImageButton: Button
    lateinit var imageView: ImageView

    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private val mAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    private var uriImage: Uri? = null

    // Receiver
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                uriImage = it.data!!.data!!
                imageView.setImageURI(uriImage)
            }
        }

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
        searchImageButton = view.findViewById(R.id.button_add_itinerary_image)
        imageView = view.findViewById(R.id.add_itinerary_imageView)

        setupAddButton()
        setupAddImageButton()

        // Inflate the layout for this fragment
        return binding
    }

    fun setupAddImageButton() {
        searchImageButton.setOnClickListener {
            selectImageFromPhone()
        }
    }

    fun selectImageFromPhone() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        try {
            getResult.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Problem to load storage images", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Call addItinerary function if the Title is usable
     */
    fun setupAddButton() {
        addButton.setOnClickListener {
            if (editTextTitle.text.toString().equals("")) {
                Toast.makeText(context, "You need to enter an itinerary name", Toast.LENGTH_SHORT).show()
            } else {
                addItinerary(editTextTitle.text.toString())
                editTextTitle.setText("")
            }
        }
    }

    /**
     * Save a new image in cloud storage if the user has selected an image
     */
    fun saveImageInFirebase(itineraryId: String,) {
        if (uriImage == null)
            return

        val imageRef = storageRef.root.child("images_itineraries/${mAuth.currentUser!!.uid}/${itineraryId}/main_image" )

        imageRef.putFile(uriImage!!)
            .addOnSuccessListener{
                Log.d("DEBUG", "Save image")
            }
            .addOnFailureListener{
                Toast.makeText(context, "Upload image failed...", Toast.LENGTH_SHORT).show()
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
                Log.d("DEBUG", "Document added")
                // Save the selected image
                saveImageInFirebase(documentReference.id)

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