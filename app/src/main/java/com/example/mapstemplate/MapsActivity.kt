package com.example.mapstemplate

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mapstemplate.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.ktx.utils.heatmaps.heatmapTileProviderWithData
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.ChoiceFormat.nextDouble
import kotlin.random.Random
import kotlin.random.Random.Default.nextFloat


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    //Map variables
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var modeToIntent : Number

    //Polygon Variables
    private lateinit var  polygon : Polygon
    private lateinit var poly : PolygonOptions

    //Search stuff
    private lateinit var searchButton : Button
    private lateinit var searchContent : TextView

    //Search results
    private lateinit var searchResult : ArrayList<String>

    //List of points
    private lateinit var highSafetyAreasOptions : ArrayList<PolygonOptions>
    private lateinit var mediumSafetyAreasOptions : ArrayList<PolygonOptions>
    private lateinit var lowSafetyAreasOptions : ArrayList<PolygonOptions>

    //List of polygons
    private lateinit var highSafetyAreas : ArrayList<Polygon>
    private lateinit var mediumSafetyAreas : ArrayList<Polygon>
    private lateinit var lowSafetyAreas : ArrayList<Polygon>

    private val trinity = LatLng(53.343792, -6.254572)

    //Variable for Log.i messages
    private val thisName = "MapsActivity"
    private lateinit var LatLngs : ArrayList<LatLng>
    private lateinit var LatLngsRed : ArrayList<LatLng>
    val tempHardCodedArray = Array(10) { Array(10) { LatLng(0.0, 0.0) } }
    private var counter : Int = 1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("mapsTag", "creating $thisName")

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchResult = ArrayList()
        tileOverlays = ArrayList()
        heatmapOverlays = ArrayList()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        searchButton = findViewById(R.id.my_button)
        searchContent = findViewById(R.id.searchText)

        searchButton.setOnClickListener{
            val searchData : String = searchContent.text.toString()
            searchPlace("Trinity");
            val intent = Intent(this, SearchResults::class.java)
            intent.putExtra("searchText", searchData)
            startActivity(intent)
        }

        modeToIntent = intent.getIntExtra("modeToIntent", 0)




        object {
            private val MAP_TYPE_KEY = "map_type"
        }

        /*val myClient = GoogleApiClient.Builder(this)
            .addApi(LocationServices.API)
            .addApi(Places.GEO_DATA_API)
            .build()*/

        //https://maps.googleapis.com/maps/api/places/nearbySearch/json?location=53.343792,-6.254572&radius=100000&type=bank&key=AIzaSyBn1QAii8KpmxExEE2WoN_89XMGhEhfx9Q

        //Lists are declared for various purposes, will probably be changed after MVP
        highSafetyAreas = ArrayList()
        mediumSafetyAreas = ArrayList()
        lowSafetyAreas = ArrayList()

        highSafetyAreasOptions = ArrayList()
        mediumSafetyAreasOptions = ArrayList()
        lowSafetyAreasOptions = ArrayList()

        //initialising poly
        poly = PolygonOptions()

        LatLngs = ArrayList()
        LatLngsRed = ArrayList()
    }

    //logging messages
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


    //Temporary changes made to help understanding of manipulating the camera
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Marker is added to trinity college, camera is zoomed in and map click listener is created
        val temp = CameraPosition.Builder()
            .target(trinity)
            .zoom(11f)
            .build()
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(temp))
        mMap.setOnMapClickListener {
            //pointsList.add(it)
            //reportArea(1, it)
            //getDangerNearArea(it, 0.1)
            //searchPlace("Restaurant")
            //heatmapDemo()
        }
        if(modeToIntent == 0){

        } else if(modeToIntent == 1){

        } else if(modeToIntent == 2){
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
        }



        //Problems for future Patrick, remove this demo data

        //mMap.animateCamera(CameraUpdateFactory.zoomTo(10f), 2000, null)
    }

    private fun searchPlace(name: String){
        var result = ""

        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
            //your codes here
            try{
                var key : String = "AIzaSyBn1QAii8KpmxExEE2WoN_89XMGhEhfx9Q"
                //key = getString(R.string.api_key)
                var urlStr : String = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=$name&key=$key"
                //https://maps.googleapis.com/maps/api/place/textsearch/json?query=Buttery&key=AIzaSyBn1QAii8KpmxExEE2WoN_89XMGhEhfx9Q
                var url : URL = URL(urlStr)
                println(url)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.setRequestProperty("Content-Type", "application/json")
                connection.requestMethod = "GET"
                connection.doInput = true
                val br = connection.inputStream.bufferedReader()
                result = br.use {br.readText()}
                //result = br.use {br.r}
                parseJsonSearch(result)
                connection.disconnect()
            } catch(e:Exception){
                e.printStackTrace()
                result = "error"
            }
            //Log.i("PlacesAPIExtra", result)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    private var heatMap = false

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.title.toString()) {
            "Settings" -> {
                // Handle settings click here
                val intent = Intent(this, MapSettings::class.java)
                startActivity(intent)
                true
            }
            "Maps Themes" -> {
                    // Handle settings click here
                val intent = Intent(this, MapsThemes::class.java)
                startActivity(intent)

                true
                }
            "Safe Mode Toggle" -> {
                // Handle settings click here
                if(heatMap){
                    removeAllHeatmaps()
                    heatMap = false
                } else {
                    heatMap = true
                    heatmapDemo()
                }
                true
            }
            // Add cases for other menu items if needed
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun nearbyPlaces(cords: LatLng, radius: Number, type : String){
        var result = ""

        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
            //your codes here
            try{
                var key : String = "AIzaSyBn1QAii8KpmxExEE2WoN_89XMGhEhfx9Q"
                //key = getString(R.string.api_key)
                var urlStr : String = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${cords.latitude},${cords.longitude}&radius=$radius&type=$type&key=$key"
                //var urlStr : String = https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=37.7749,-122.4194&radius=500&type=restaurant&key=AIzaSyBn1QAii8KpmxExEE2WoN_89XMGhEhfx9Q
                println("$urlStr")
                var url : URL = URL(urlStr)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.setRequestProperty("Content-Type", "application/json")
                connection.requestMethod = "GET"
                connection.doInput = true
                val br = connection.inputStream.bufferedReader()
                result = br.use {br.readText()}
                //result = br.use {br.r}
                parseJson(result)
                connection.disconnect()
            } catch(e:Exception){
                e.printStackTrace()
                result = "error"
            }
            //Log.i("mapsTag", result)
        }
    }

    private fun parseJson(jsonString: String) {
        val jsonObject = JSONObject(jsonString)
        val resultsArray: JSONArray = jsonObject.getJSONArray("results")

        for (i in 0 until resultsArray.length()) {
            val resultObject = resultsArray.getJSONObject(i)
            val name = resultObject.getString("name")
            val rating = resultObject.getDouble("rating")
            val vicinity = resultObject.getString("vicinity")

            Log.i("PlacesAPI", "====================================================")
            Log.i("PlacesAPI", "Name: $name")
            Log.i("PlacesAPI", "Rating: $rating")
            Log.i("PlacesAPI", "Vicinity: $vicinity")
        }
    }

    private var tileOverlays = mutableListOf<TileOverlay>()
    private var heatmapOverlays = mutableListOf<TileOverlay>()

    private fun removeAllHeatmaps() {
        for (overlay in heatmapOverlays) {
            overlay.remove()
        }
        // Clear the list of TileOverlay objects
        heatmapOverlays.clear()
    }



    private fun parseJsonSearch(jsonString: String) {
        val jsonObject = JSONObject(jsonString)
        val resultsArray: JSONArray = jsonObject.getJSONArray("results")

        for (i in 0 until resultsArray.length()) {
            val resultObject = resultsArray.getJSONObject(i)
            val name = resultObject.getString("name")

            Log.i("PlacesAPI", "====================================================")
            Log.i("PlacesAPI", "Name: $name")
            searchResult.add(name)
        }
    }

    //temporary variables to be deleted later


    private fun reportArea(danger: Number, cords : LatLng){
        tempHardCodedArray[danger as Int][0] = cords
        tempHardCodedArray[danger as Int][1] = cords
        tempHardCodedArray[danger as Int][2] = cords
        counter += 1
        if(counter == 8){
            counter = 1;
        }
        //ToDo
    }

    private lateinit var provider : HeatmapTileProvider

    private fun getDangerNearArea(cords: LatLng, radius: Number){
        //ToDo
        //Temporary hard coded Array for testing purposes.
        for(i in 1 .. 7){
            val colors = intArrayOf(
                safetyColors[i]
            )
            val mutableList = tempHardCodedArray[i].toMutableList()
            //val mutableList = arrayListOf<LatLng>(cords)
            val startPoints = floatArrayOf(1f)
            val gradient = Gradient(colors, startPoints)
            provider = HeatmapTileProvider.Builder()
                .data(mutableList)
                .gradient(gradient)
                .opacity(1.0)
                .build()
            val lastOverlay = mMap.addTileOverlay(TileOverlayOptions().tileProvider(provider))
            if (lastOverlay != null) {
                heatmapOverlays.add(lastOverlay)
            }
            tempHardCodedArray[i].fill(LatLng(0.0, 0.0))
            //lastOverlay = mMap.
        }


        // Add a tile overlay to the map, using the heat map tile provider./


    }

    //Function to change the colour of a certain area of the map
    private fun demoReportArea(danger: Number, cords : LatLng){
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
    val safetyColors = intArrayOf(
        Color.argb(0,50,50,50),
        Color.argb(255,255,0,0),
        Color.argb(255,255,165,0),
        Color.argb(255,255,130,0),
        Color.argb(255,245,174,66),
        Color.argb(255,255,255,15),
        Color.argb(255,154,205,50),
        Color.argb(255,0,255,0)
    )
    private var transparent = Color.argb(0,50,50,50)
    private var opaqueRed = Color.argb(100,255,0,0)
    private var opaqueOrange = Color.argb(100,255,165,0)
    private var opaqueDarkOrange = Color.argb(100,255,130,0)
    private var opaqueOrangeyYellow = Color.argb(100,245,174,66)
    private var opaqueYellow = Color.argb(100,255,255,15)
    private var opaqueYelloweyGreen = Color.argb(100,154,205,50)
    private var opaqueGreen = Color.argb(100,0,255,0)


    //Function currently redundant, will probably be altered and used again in the future
    private fun countPolygonPoints() {
        //Function to draw Polygon
        if (poly.points.size > 3) {
            poly.strokeColor(transparent)
            poly.fillColor(opaqueRed)
            polygon = mMap.addPolygon(poly)
        }
    }

    private fun heatmapDemo(){
        var i :Number
        //North of trinity and to the east
        for(i in 0..20){
            val radius = 0.05
            //reportArea(1, LatLng(trinity.latitude + (nextDouble(0.1) % 0), trinity.longitude + (nextDouble(0.1) % 0)))
            val trinity = LatLng(53.383792, -6.224572)
            val tempVal1 = Random.nextFloat() % radius
            val randomValue1 = (Random.nextFloat() % tempVal1) - (tempVal1 / 2)
            //randomValue1 = 0.0
            val trinityX = trinity.latitude + randomValue1 // Generate a random number between -0.1 and 0.1
            val tempVal2 = Random.nextFloat() % radius
            val randomValue2 = (Random.nextFloat() % tempVal2) - (tempVal2 / 2)
            val trinityY = trinity.longitude + randomValue2

            //reportArea(1, LatLng(trinity.latitude + (nextFloat() % 0.01), trinity.longitude + (nextFloat() % 0.01)))
            reportArea(6, LatLng(trinityX, trinityY))
            getDangerNearArea(trinity, 1000)
        }
        //Trinity Green and to the west
        for(i in 0..20){
            val radius = 0.06
            //reportArea(1, LatLng(trinity.latitude + (nextDouble(0.1) % 0), trinity.longitude + (nextDouble(0.1) % 0)))
            val trinity = LatLng(53.373792, -6.269572)
            val tempVal1 = Random.nextFloat() % radius
            val randomValue1 = (Random.nextFloat() % tempVal1) - (tempVal1 / 2)
            //randomValue1 = 0.0
            val trinityX = trinity.latitude + randomValue1 // Generate a random number between -0.1 and 0.1
            val tempVal2 = Random.nextFloat() % radius
            val randomValue2 = (Random.nextFloat() % tempVal2) - (tempVal2 / 2)
            val trinityY = trinity.longitude + randomValue2

            //reportArea(1, LatLng(trinity.latitude + (nextFloat() % 0.01), trinity.longitude + (nextFloat() % 0.01)))
            reportArea(4, LatLng(trinityX, trinityY))
            getDangerNearArea(trinity, 1000)
        }
        //Trinity Green and to the west a lot
        for(i in 0..20){
            val radius = 0.08
            //reportArea(1, LatLng(trinity.latitude + (nextDouble(0.1) % 0), trinity.longitude + (nextDouble(0.1) % 0)))
            val trinity = LatLng(53.373792, -6.274572)
            val tempVal1 = Random.nextFloat() % radius
            val randomValue1 = (Random.nextFloat() % tempVal1) - (tempVal1 / 2)
            //randomValue1 = 0.0
            val trinityX = trinity.latitude + randomValue1 // Generate a random number between -0.1 and 0.1
            val tempVal2 = Random.nextFloat() % radius
            val randomValue2 = (Random.nextFloat() % tempVal2) - (tempVal2 / 2)
            val trinityY = trinity.longitude + randomValue2

            //reportArea(1, LatLng(trinity.latitude + (nextFloat() % 0.01), trinity.longitude + (nextFloat() % 0.01)))
            reportArea(2, LatLng(trinityX, trinityY))
            getDangerNearArea(trinity, 1000)
        }

        //Trinity Green
        for(i in 0..20){
            val radius = 0.08
            //reportArea(1, LatLng(trinity.latitude + (nextDouble(0.1) % 0), trinity.longitude + (nextDouble(0.1) % 0)))
            val trinity = LatLng(53.343792, -6.254572)
            val tempVal1 = Random.nextFloat() % radius
            val randomValue1 = (Random.nextFloat() % tempVal1) - (tempVal1 / 2)
            //randomValue1 = 0.0
            val trinityX = trinity.latitude + randomValue1 // Generate a random number between -0.1 and 0.1
            val tempVal2 = Random.nextFloat() % radius
            val randomValue2 = (Random.nextFloat() % tempVal2) - (tempVal2 / 2)
            val trinityY = trinity.longitude + randomValue2

            //reportArea(1, LatLng(trinity.latitude + (nextFloat() % 0.01), trinity.longitude + (nextFloat() % 0.01)))
            reportArea(7, LatLng(trinityX, trinityY))
            getDangerNearArea(trinity, 1000)
        }


        //Trinity Green and to the a lot but a little less and a bit north
        for(i in 0..20){
            val radius = 0.04
            //reportArea(1, LatLng(trinity.latitude + (nextDouble(0.1) % 0), trinity.longitude + (nextDouble(0.1) % 0)))
            val trinity = LatLng(53.393792, -6.264572)
            val tempVal1 = Random.nextFloat() % radius
            val randomValue1 = (Random.nextFloat() % tempVal1) - (tempVal1 / 2)
            //randomValue1 = 0.0
            val trinityX = trinity.latitude + randomValue1 // Generate a random number between -0.1 and 0.1
            val tempVal2 = Random.nextFloat() % radius
            val randomValue2 = (Random.nextFloat() % tempVal2) - (tempVal2 / 2)
            val trinityY = trinity.longitude + randomValue2

            //reportArea(1, LatLng(trinity.latitude + (nextFloat() % 0.01), trinity.longitude + (nextFloat() % 0.01)))
            reportArea(1, LatLng(trinityX, trinityY))
            getDangerNearArea(trinity, 1000)
        }

        // IFSC GreenyYellow
        for(i in 0..20){
            val radius = 0.06
            //reportArea(1, LatLng(trinity.latitude + (nextDouble(0.1) % 0), trinity.longitude + (nextDouble(0.1) % 0)))
            val tempLocation = LatLng(53.3295, -6.2155)
            val tempVal1 = Random.nextFloat() % radius
            val randomValue1 = (Random.nextFloat() % tempVal1) - (tempVal1 / 2)
            //randomValue1 = 0.0
            val trinityX = tempLocation.latitude + randomValue1 // Generate a random number between -0.1 and 0.1
            val tempVal2 = Random.nextFloat() % radius
            val randomValue2 = (Random.nextFloat() % tempVal2) - (tempVal2 / 2)
            val trinityY = tempLocation.longitude + randomValue2

            //reportArea(1, LatLng(trinity.latitude + (nextFloat() % 0.01), trinity.longitude + (nextFloat() % 0.01)))
            reportArea(6, LatLng(trinityX, trinityY))
            getDangerNearArea(trinity, 1000)
        }

        // UCD Yellow
        for(i in 0..20){
            val radius = 0.05
            //reportArea(1, LatLng(trinity.latitude + (nextDouble(0.1) % 0), trinity.longitude + (nextDouble(0.1) % 0)))
            val tempLocation = LatLng(53.3065, -6.2187)
            val tempVal1 = Random.nextFloat() % radius
            val randomValue1 = (Random.nextFloat() % tempVal1) - (tempVal1 / 2)
            //randomValue1 = 0.0
            val trinityX = tempLocation.latitude + randomValue1 // Generate a random number between -0.1 and 0.1
            val tempVal2 = Random.nextFloat() % radius
            val randomValue2 = (Random.nextFloat() % tempVal2) - (tempVal2 / 2)
            val trinityY = tempLocation.longitude + randomValue2

            //reportArea(1, LatLng(trinity.latitude + (nextFloat() % 0.01), trinity.longitude + (nextFloat() % 0.01)))
            reportArea(4, LatLng(trinityX, trinityY))
            getDangerNearArea(trinity, 1000)
        }

        // Left of UCD Orangey Yellow
        for(i in 0..20){
            val radius = 0.08
            //reportArea(1, LatLng(trinity.latitude + (nextDouble(0.1) % 0), trinity.longitude + (nextDouble(0.1) % 0)))
            val tempLocation = LatLng(53.3165, -6.2687)
            val tempVal1 = Random.nextFloat() % radius
            val randomValue1 = (Random.nextFloat() % tempVal1) - (tempVal1 / 2)
            //randomValue1 = 0.0
            val trinityX = tempLocation.latitude + randomValue1 // Generate a random number between -0.1 and 0.1
            val tempVal2 = Random.nextFloat() % radius
            val randomValue2 = (Random.nextFloat() % tempVal2) - (tempVal2 / 2)
            val trinityY = tempLocation.longitude + randomValue2

            //reportArea(1, LatLng(trinity.latitude + (nextFloat() % 0.01), trinity.longitude + (nextFloat() % 0.01)))
            reportArea(3, LatLng(trinityX, trinityY))
            getDangerNearArea(trinity, 1000)
        }

        // Southof Left of UCD Orangey Yellow
        for(i in 0..20){
            val radius = 0.08
            //reportArea(1, LatLng(trinity.latitude + (nextDouble(0.1) % 0), trinity.longitude + (nextDouble(0.1) % 0)))
            val tempLocation = LatLng(53.2565, -6.2887)
            val tempVal1 = Random.nextFloat() % radius
            val randomValue1 = (Random.nextFloat() % tempVal1) - (tempVal1 / 2)
            //randomValue1 = 0.0
            val trinityX = tempLocation.latitude + randomValue1 // Generate a random number between -0.1 and 0.1
            val tempVal2 = Random.nextFloat() % radius
            val randomValue2 = (Random.nextFloat() % tempVal2) - (tempVal2 / 2)
            val trinityY = tempLocation.longitude + randomValue2

            //reportArea(1, LatLng(trinity.latitude + (nextFloat() % 0.01), trinity.longitude + (nextFloat() % 0.01)))
            reportArea(1, LatLng(trinityX, trinityY))
            getDangerNearArea(trinity, 1000)
        }

        // North of Left of UCD Orangey Red
        for(i in 0..20){
            val radius = 0.08
            //reportArea(1, LatLng(trinity.latitude + (nextDouble(0.1) % 0), trinity.longitude + (nextDouble(0.1) % 0)))
            val tempLocation = LatLng(53.3065, -6.3187)
            val tempVal1 = Random.nextFloat() % radius
            val randomValue1 = (Random.nextFloat() % tempVal1) - (tempVal1 / 2)
            //randomValue1 = 0.0
            val trinityX = tempLocation.latitude + randomValue1 // Generate a random number between -0.1 and 0.1
            val tempVal2 = Random.nextFloat() % radius
            val randomValue2 = (Random.nextFloat() % tempVal2) - (tempVal2 / 2)
            val trinityY = tempLocation.longitude + randomValue2

            //reportArea(1, LatLng(trinity.latitude + (nextFloat() % 0.01), trinity.longitude + (nextFloat() % 0.01)))
            reportArea(2, LatLng(trinityX, trinityY))
            getDangerNearArea(trinity, 1000)
        }


    }

}