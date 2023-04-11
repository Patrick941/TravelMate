package com.example.mapstemplate.activities

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapstemplate.R
import com.example.mapstemplate.adapters.ImageRecyclerViewAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.component1
import java.io.File
import java.io.IOException
import java.util.UUID


class ImagesItineraryVisualisationActivity : AppCompatActivity() {
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private var mAuth = FirebaseAuth.getInstance()

    lateinit var addImageButton: FloatingActionButton
    lateinit var recyclerView: RecyclerView
    lateinit var backButton: ImageView

    lateinit var itineraryName: String
    private val imagesList = ArrayList<File>()

    // Receiver
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                val uri: Uri = it.data!!.data!!
                saveImageInFirebase(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images_itinerary_visualisation)

        itineraryName = intent.getStringExtra("itinerary_name")!!

        addImageButton = findViewById(R.id.button_add_image)
        recyclerView = findViewById(R.id.images_displayer)
        backButton = findViewById(R.id.back_arrow_image_activity)

        // setup recycler view
        recyclerView.adapter = ImageRecyclerViewAdapter(imagesList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Finish the activity on click
        backButton.setOnClickListener {
            finish()
        }

        Log.d("DEBUG", "START VISUAL")
        fetchTestImages()
        setupAddImageButton()
    }

    fun fetchTestImages() {
        val imagesRef = storageRef.root.child("images_itineraries/${mAuth.currentUser!!.uid}/${itineraryName}")

        imagesRef.listAll()
            .addOnSuccessListener { (items) ->
                items.forEach { item ->
                    Log.d("DEBUG", item.name)
                    fetchSpecificImage(item)
                }
            }
            .addOnFailureListener {
                Log.d("DEBUG", "FAIL")
            }
    }

    fun fetchSpecificImage(storageRef: StorageReference) {
        try {
            val localfile = File.createTempFile(storageRef.name, ".jpg")
            storageRef.getFile(localfile).addOnSuccessListener {
                imagesList.add(localfile)

                recyclerView.adapter?.notifyItemInserted(imagesList.size-1)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Save a new image in cloud storage.
     * THE FILE NAME MUST BE UNIQUE
     */
    fun saveImageInFirebase(uri: Uri) {
        val imageRef = storageRef.root.child("images_itineraries/${mAuth.currentUser!!.uid}/${itineraryName}/${UUID.randomUUID()}" )

        imageRef.putFile(uri)
            .addOnSuccessListener{
            Toast.makeText(this, "Upload successful :)", Toast.LENGTH_SHORT).show()
                val localfile = File.createTempFile(it.storage.name, ".jpg")
                it.storage.getFile(localfile).addOnSuccessListener {
                    imagesList.add(localfile)
                    recyclerView.adapter?.notifyItemInserted(imagesList.size-1)
                }
        }
            .addOnFailureListener{
            Toast.makeText(this, "Upload failed...", Toast.LENGTH_SHORT).show()
        }
    }

    fun setupAddImageButton() {
        addImageButton.setOnClickListener {
            selectImageFromPhone()
        }
    }

    fun selectImageFromPhone() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        getResult.launch(intent)
    }


}