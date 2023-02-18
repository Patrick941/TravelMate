package com.example.mapstemplate

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mapstemplate.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import kotlin.random.Random

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    //Map variables
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    //Polygon Variables
    private lateinit var  polygon : Polygon
    private lateinit var poly : PolygonOptions

    //List of points
    private lateinit var highSafetyAreasOptions : ArrayList<PolygonOptions>
    private lateinit var mediumSafetyAreasOptions : ArrayList<PolygonOptions>
    private lateinit var lowSafetyAreasOptions : ArrayList<PolygonOptions>

    //List of polygons
    private lateinit var highSafetyAreas : ArrayList<Polygon>
    private lateinit var mediumSafetyAreas : ArrayList<Polygon>
    private lateinit var lowSafetyAreas : ArrayList<Polygon>

    //Variable for Log.i messages
    private val thisName = "MapsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("mapsTag", "creating $thisName")

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        highSafetyAreas = ArrayList()
        mediumSafetyAreas = ArrayList()
        lowSafetyAreas = ArrayList()

        highSafetyAreasOptions = ArrayList()
        mediumSafetyAreasOptions = ArrayList()
        lowSafetyAreasOptions = ArrayList()

        poly = PolygonOptions()
    }

    override fun onPause(){
        super.onPause()
        Log.i("MyTag", "pausing $thisName")
    }

    override fun onResume(){
        super.onResume()
        Log.i("MyTag", "resuming $thisName")
    }

    override fun onStart(){
        super.onStart()
        Log.i("MyTag", "starting $thisName")
    }

    override fun onStop(){
        super.onStop()
        Log.i("MyTag", "stopping $thisName")
    }

    override fun onRestart(){
        super.onRestart()
        Log.i("MyTag", "restarting $thisName")
    }

    override fun onDestroy(){
        super.onDestroy()
        Log.i("MyTag", "destroying $thisName")
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Marker is added to trinity college, camera is zoomed in and map click listener is created
        val trinity = LatLng(53.343792, -6.254572)
        val temp = CameraPosition.Builder()
            .target(trinity)
            .zoom(13f)
            .build()
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(temp))
        mMap.setOnMapClickListener {
            //pointsList.add(it)
            reportArea(3, it)

        }
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(10f), 2000, null)
    }

    private fun reportArea(danger: Number, cords : LatLng){
        val precision = 0.001
        val cordsNorth = LatLng(cords.latitude + precision, cords.longitude)
        val cordsSouth = LatLng(cords.latitude - precision, cords.longitude)
        val cordsWest = LatLng(cords.latitude, cords.longitude - precision)
        val cordsEast = LatLng(cords.latitude, cords.longitude + precision)
        when (danger) {
            1 -> {
                val temp : PolygonOptions = PolygonOptions()
                temp.add(cordsNorth)
                temp.add(cordsEast)
                temp.add(cordsSouth)
                temp.add(cordsWest)
                temp.strokeColor(transparent)
                temp.fillColor(opaqueGreen)
                highSafetyAreas.add(mMap.addPolygon(temp))
                Log.i("mapsTag", "danger level 1 area added")
            }
            2 -> {
                val temp : PolygonOptions = PolygonOptions()
                temp.add(cordsNorth)
                temp.add(cordsEast)
                temp.add(cordsSouth)
                temp.add(cordsWest)
                temp.strokeColor(transparent)
                temp.fillColor(opaqueYelloweyGreen)
                highSafetyAreas.add(mMap.addPolygon(temp))
                Log.i("mapsTag", "danger level 2 area added")
            }
            3 -> {
                val temp : PolygonOptions = PolygonOptions()
                temp.add(cordsNorth)
                temp.add(cordsEast)
                temp.add(cordsSouth)
                temp.add(cordsWest)
                temp.strokeColor(transparent)
                temp.fillColor(opaqueYellow)
                highSafetyAreas.add(mMap.addPolygon(temp))
                Log.i("mapsTag", "danger level 3 area added")
            }
            4 -> {
                val temp : PolygonOptions = PolygonOptions()
                temp.add(cordsNorth)
                temp.add(cordsEast)
                temp.add(cordsSouth)
                temp.add(cordsWest)
                temp.strokeColor(transparent)
                temp.fillColor(opaqueOrangeyYellow)
                highSafetyAreas.add(mMap.addPolygon(temp))
                Log.i("mapsTag", "danger level 4 area added")
            }
            5 -> {
                val temp : PolygonOptions = PolygonOptions()
                temp.add(cordsNorth)
                temp.add(cordsEast)
                temp.add(cordsSouth)
                temp.add(cordsWest)
                temp.strokeColor(transparent)
                temp.fillColor(opaqueOrange)
                highSafetyAreas.add(mMap.addPolygon(temp))
                Log.i("mapsTag", "danger level 5 area added")
            }
            6 -> {
                val temp : PolygonOptions = PolygonOptions()
                temp.add(cordsNorth)
                temp.add(cordsEast)
                temp.add(cordsSouth)
                temp.add(cordsWest)
                temp.strokeColor(transparent)
                temp.fillColor(opaqueDarkOrange)
                highSafetyAreas.add(mMap.addPolygon(temp))
                Log.i("mapsTag", "danger level 6 area added")
            }
            7 -> {
                val temp : PolygonOptions = PolygonOptions()
                temp.add(cordsNorth)
                temp.add(cordsEast)
                temp.add(cordsSouth)
                temp.add(cordsWest)
                temp.strokeColor(transparent)
                temp.fillColor(opaqueRed)
                highSafetyAreas.add(mMap.addPolygon(temp))
                Log.i("mapsTag", "danger level 7 area added")
            }
            else -> {
                Toast.makeText(this, "unsupported rating provided", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Colour variables to adjust colours of areas to represent level of safety
    private var transparent = Color.argb(0,50,50,50)
    private var opaqueRed = Color.argb(100,255,0,0)
    private var opaqueOrange = Color.argb(100,255,165,0)
    private var opaqueDarkOrange = Color.argb(100,255,130,0)
    private var opaqueOrangeyYellow = Color.argb(100,245,174,66)
    private var opaqueYellow = Color.argb(100,255,255,15)
    private var opaqueYelloweyGreen = Color.argb(100,154,205,50)
    private var opaqueGreen = Color.argb(100,0,255,0)
    private fun countPolygonPoints() {
        //Function to draw Polygon
        if (poly.points.size > 3) {
            poly.strokeColor(transparent)
            poly.fillColor(opaqueRed)
            polygon = mMap.addPolygon(poly)
        }
    }

}