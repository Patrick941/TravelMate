package com.example.mapstemplate

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.Menu
import android.widget.Button
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.mapstemplate.databinding.ActivityHomeBinding
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import com.example.travelapp.itineraries.Itinerary
import com.example.travelapp.itineraries.Step
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding

    private val thisName = "HomeActivity"

    private lateinit var mapButton : Button
    private val trinity = LatLng(53.343792, -6.254572)

    private val db = Firebase.firestore
    private lateinit var mAuth : FirebaseAuth

    companion object {
        val currentUserItineraryList = ArrayList<Itinerary>();
        val globalItineraryList = ArrayList<Itinerary>();
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        Log.i("MyTag", "creating $thisName")
        setSupportActionBar(binding.appBarHome.toolbar)

        //variables assigned to different aspects of the view
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        mapButton = findViewById(R.id.mapButton)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_history, R.id.nav_Contacts, R.id.nav_my_itineraries, R.id.nav_global_itineraries
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //Detect when map button is pressed and follow intent to map
        mapButton.setOnClickListener{
            val intent = Intent(this@HomeActivity, MapsActivity::class.java)
            startActivity(intent)
        }

        fetchCurrentUserItineraries()
        fetchGlobalItineraries()
    }

    // get current user itineraries from firestore
    fun fetchCurrentUserItineraries() {
        // clear previous data to prevent bug
        currentUserItineraryList.clear()

        db.collection("itineraries")
            .whereEqualTo("user_email", mAuth.currentUser!!.email)
            .get()
            .addOnSuccessListener { itineraries ->
                storeFetchedItinerariesInList(currentUserItineraryList, itineraries)
            }
            .addOnFailureListener { exception ->
                Log.w("DEBUG", "Error getting documents.", exception)
            }
    }

    // get all itineraries from firestore
    fun fetchGlobalItineraries() {
        // clear previous data to prevent bug
        globalItineraryList.clear()

        db.collection("itineraries")
            .whereNotEqualTo("user_email", mAuth.currentUser!!.email)
            .get()
            .addOnSuccessListener { itineraries ->
                storeFetchedItinerariesInList(globalItineraryList, itineraries)
            }
            .addOnFailureListener { exception ->
                Log.w("DEBUG", "Error getting documents.", exception)
            }
    }

    private fun storeFetchedItinerariesInList(list: ArrayList<Itinerary>, querySnapshot: QuerySnapshot) {
        for (itineraryDocument in querySnapshot) {
            val itinerary = Itinerary(
                itineraryDocument.data.get("title") as String,
                itineraryDocument.id
            )

            db.collection("itineraries/${itineraryDocument.id}/steps")
                .get()
                .addOnSuccessListener { steps ->
                    for (stepDocument in steps) {
                        val step = Step(
                            stepDocument.data.get("name") as String,
                            stepDocument.data.get("address") as String,
                            stepDocument.data.get("price") as Double,
                            stepDocument.data.get("description") as String
                        )
                        itinerary.steps.add(step)
                    }
                }

            list.add(itinerary)
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
            //nearbyLocations.add(name)
            Log.i("PlacesAPI", "Name: $name")
            Log.i("PlacesAPI", "Rating: $rating")
            Log.i("PlacesAPI", "Vicinity: $vicinity")
        }
    }


    private fun nearbyPlaces(cords: LatLng, radius: Number, type : String){
        var result = ""

        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
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

    // Logging messages
    override fun onPause(){
        super.onPause()
        Log.i("MyTag", "pausing $thisName")
    }

    //Functions to hide parts of the UI when app is running, not yet correctly implemented
    override fun onResume(){
        super.onResume()
        hideSystemUI()
        setTheme(R.style.Theme_MapsTemplate)
        nearbyPlaces(trinity, 1000, "restaurant")
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

    //Function does not operate fully correctly, only does part of job, will need to be updated
    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            //controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.hide(WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}