package com.example.mapstemplate.activities

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.example.mapstemplate.HomeActivity
import com.example.mapstemplate.R
import com.example.travelapp.itineraries.Step
import com.google.firebase.firestore.FirebaseFirestore

class StepViewActivity : AppCompatActivity() {
    private var isLiked = false;
    lateinit var back_arrow: ImageView
    lateinit var deleteButton: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_view)

        val itineraryIndex: Int = intent.getIntExtra("itinerary_index", 0)
        val stepIndex: Int = intent.getIntExtra("step_index", 0)
        val step: Step = HomeActivity.currentUserItineraryList[itineraryIndex].steps[stepIndex]

        back_arrow = findViewById(R.id.back_arrow_display_step)
        deleteButton = findViewById(R.id.button_delete_step)

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
        }

        deleteButton.setOnClickListener {
            warningDeletePopup()
        }


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
