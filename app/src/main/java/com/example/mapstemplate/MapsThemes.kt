package com.example.mapstemplate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.mapstemplate.databinding.ActivityMapsBinding

class MapsThemes : AppCompatActivity() {

    private var modeToIntent = 0

    private lateinit var darkButton : Button
    private lateinit var lightButton : Button
    private lateinit var apply : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_themes)




        lightButton = findViewById(R.id.LightMode)
        darkButton = findViewById(R.id.DarkMode)
        apply = findViewById(R.id.Apply)

        lightButton.setOnClickListener{
            modeToIntent = 1
        }

        darkButton.setOnClickListener{
            modeToIntent = 2
        }

        apply.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("modeToIntent", modeToIntent)
            startActivity(intent)
        }
    }
}