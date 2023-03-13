package com.example.mapstemplate.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mapstemplate.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class DisplayStepMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private var isPermissionGranted = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_step_map)

        checkPermission()

        if (!isPermissionGranted) {
            val supportMapFragment = supportFragmentManager.findFragmentById(R.id.step_map_frag) as SupportMapFragment
            supportMapFragment.getMapAsync(this)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {

    }

    fun checkPermission() {

    }
}