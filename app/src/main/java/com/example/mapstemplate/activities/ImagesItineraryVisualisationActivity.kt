package com.example.mapstemplate.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.mapstemplate.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.component1
import java.io.File
import java.io.IOException


class ImagesItineraryVisualisationActivity : AppCompatActivity() {
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private var mAuth = FirebaseAuth.getInstance()

    lateinit var imageView: ImageView
    lateinit var addImageButton: FloatingActionButton

    lateinit var itineraryName: String
    private val imagesList = ArrayList<File>()

    // Receiver
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                saveImageInFirebase(it.data!!.data!!)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images_itinerary_visualisation)

        itineraryName = intent.getStringExtra("itinerary_name")!!

        imageView = findViewById(R.id.display_image)
        addImageButton = findViewById(R.id.button_add_image)

        Log.d("DEBUG", "START VISUAL")
        fetchTestImages()
        setupAddImageButton()
    }

    fun fetchTestImages() {
        val imagesRef = storageRef.root.child("images_itineraries")

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

                // set an image to the imageView
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                imageView.setImageBitmap(bitmap)
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
        val imageRef = storageRef.root.child("images_itineraries/${mAuth.currentUser!!.uid}/${itineraryName}" )

        imageRef.putFile(uri)
            .addOnSuccessListener{
            imageView.setImageURI(uri)
            Toast.makeText(this, "Upload successful :)", Toast.LENGTH_SHORT).show()
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