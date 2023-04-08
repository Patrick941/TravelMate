package com.example.mapstemplate.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.mapstemplate.HomeActivity
import com.example.mapstemplate.R
import com.example.travelapp.itineraries.Step

class StepViewActivity : AppCompatActivity() {
    private var isLiked = false;
    lateinit var back_arrow: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_view)

        val itineraryIndex: Int = intent.getIntExtra("itinerary_index", 0)
        val stepIndex: Int = intent.getIntExtra("step_index", 0)
        val step: Step = HomeActivity.currentUserItineraryList[itineraryIndex].steps[stepIndex]

        back_arrow = findViewById(R.id.back_arrow_display_step)

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
    }

