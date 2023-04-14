package com.example.mapstemplate.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapstemplate.HomeActivity
import com.example.mapstemplate.R
import com.example.mapstemplate.adapters.ImageRecyclerViewAdapter
import com.example.travelapp.itineraries.Itinerary
import com.example.travelapp.itineraries.Step
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.component1
import java.io.File
import java.util.*


class StepViewActivity : AppCompatActivity() {
    private var isLiked = false
    lateinit var back_arrow: ImageView
    lateinit var deleteButton: ImageView
    lateinit var addImageButton: FloatingActionButton
    lateinit var recyclerView: RecyclerView
    lateinit var linearLayoutPlaceHolder: LinearLayout

    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private var mAuth = FirebaseAuth.getInstance()

    private lateinit var itinerary: Itinerary
    private lateinit var step: Step
    private val imageList = ArrayList<File>()

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
        setContentView(R.layout.activity_step_view)

        val itineraryIndex: Int = intent.getIntExtra("itinerary_index", 0)
        val stepIndex: Int = intent.getIntExtra("step_index", 0)
        val isGlobal: Boolean = intent.getBooleanExtra("is_global", true)

        // Fetch itinerary and step from companion list
        if (isGlobal)
            itinerary = HomeActivity.globalItineraryList[itineraryIndex]
        else
            itinerary = HomeActivity.currentUserItineraryList[itineraryIndex]

        step = itinerary.steps[stepIndex]

        back_arrow = findViewById(R.id.back_arrow_display_step)
        deleteButton = findViewById(R.id.button_delete_step)
        addImageButton = findViewById(R.id.button_add_step_image)
        recyclerView = findViewById(R.id.recycler_view_image_step)
        linearLayoutPlaceHolder = findViewById(R.id.placeholder_step_image_layout)

        // Remove delete icone if it's a global itinerary
        if (isGlobal) {
            deleteButton.isVisible = false
        }

        // Recycler view not visible until ther is item in it
        recyclerView.isVisible = false

        // setup recycler view
        recyclerView.adapter = ImageRecyclerViewAdapter(imageList)

        // Fetch images from firebase
        fetchStepImages()

        val name = findViewById<TextView>(R.id.textView_title_step_display)
        name.text = step.name

        val mainName = findViewById<TextView>(R.id.main_name) as TextView
        mainName.text = step.name

        val address = findViewById<TextView>(R.id.address)
        address.text = step.address

        val price = findViewById<TextView>(R.id.price)
        price.text = "${step.price} €"

        val description = findViewById<TextView>(R.id.description)
        description.text = step.description

        val likeButton = findViewById<ImageView>(R.id.heart)
        likeButton.setOnClickListener {
            isLiked = !isLiked
            if (isLiked) {
                likeButton.setImageResource(R.drawable.filled_heart)
            } else {
                likeButton.setImageResource(R.drawable.heart)
            }


            // You should also set up the ImageViews for your new XML layout here
            // I'm not doing this here because it depends on your implementation
        }

        back_arrow.setOnClickListener {
            finish()
        }

        // allow deletion if it's a user itinerary step
        if (!isGlobal) {
            deleteButton.setOnClickListener {
                warningDeletePopup()
            }
        }

        setupAddImageButton()
    }

    fun fetchStepImages() {
        val imagesRef = storageRef.root.child("images_itineraries/${itinerary.id}/${step.id}")

        imagesRef.listAll()
            .addOnSuccessListener { (items) ->
                items.forEach { item ->
                    fetchSpecificImage(item)
                }
            }
            .addOnFailureListener {
                Log.d("DEBUG", "FAIL")
            }
    }

    fun fetchSpecificImage(imageRef: StorageReference) {
        try {
            val localfile = File.createTempFile("${step.id}_${imageRef.name}", ".jpg")
            imageRef.getFile(localfile).addOnSuccessListener {
                // Add image in local list and notify recyclerView for update
                imageList.add(localfile)
                recyclerView.adapter?.notifyItemInserted(imageList.size-1)
                Log.d("DEBUG", "Fetch step image : ${imageRef.name}")

                // Change visibility if recycler view not visible
                if (!recyclerView.isVisible) {
                    recyclerView.isVisible = true
                    linearLayoutPlaceHolder.isVisible = false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Save a new image in cloud storage.
     * THE FILE NAME MUST BE UNIQUE
     */
    fun saveImageInFirebase(uri: Uri) {
        val imageRef = storageRef.root.child("images_itineraries/${itinerary.id}/${step.id}/${UUID.randomUUID()}")

        imageRef.putFile(uri)
            .addOnSuccessListener{
                Toast.makeText(this, "Upload successful :)", Toast.LENGTH_SHORT).show()
                fetchSpecificImage(it.storage)
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


    fun warningDeletePopup() {
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setMessage("Are you sure you want to delete this step ?")
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.dismiss()
        })
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Delete", DialogInterface.OnClickListener { dialogInterface, i ->
            deleteStepFromFirebase()
            dialogInterface.dismiss()
            finish()
        })
        alertDialog.show()
    }

    fun deleteStepFromFirebase() {
        Log.d("DEBUG", "deleteStepFromFirebase")
        val itineraryIndex: Int = intent.getIntExtra("itinerary_index", 0)
        val stepIndex: Int = intent.getIntExtra("step_index", 0)
        val step: Step = HomeActivity.currentUserItineraryList[itineraryIndex].steps[stepIndex]

        val itineraryId = HomeActivity.currentUserItineraryList[itineraryIndex].id
        val stepId = HomeActivity.currentUserItineraryList[itineraryIndex].steps[stepIndex].id



        val TAG = "StepViewActivity"
        val db = FirebaseFirestore.getInstance()


        db.collection("itineraries")
            .document(itineraryId)
            .collection("steps")
            .document(stepId)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Step successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting step", e)
            }

        // delete step in list
        HomeActivity.currentUserItineraryList[itineraryIndex].steps.removeAt(stepIndex)
    }
}
