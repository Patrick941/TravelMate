package com.example.mapstemplate

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.Menu
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mapstemplate.databinding.ActivityHomeBinding
import com.example.mapstemplate.itineraries.UserRate
import com.example.mapstemplate.ui.contacts.ContactsFragment
import com.example.mapstemplate.ui.current_user_itineraries.CurrentUserItinerariesFragment
import com.example.mapstemplate.ui.global_itineraries.GlobalItinerariesFragment
import com.example.mapstemplate.ui.history.HistoryFragment
import com.example.mapstemplate.ui.home.HomeFragment
import com.example.travelapp.itineraries.Itinerary
import com.example.travelapp.itineraries.Step
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL


class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private lateinit var bottomNavigationView: BottomNavigationView

    private var tempString : String? = null

    private val thisName = "HomeActivity"

    private lateinit var mapButton : Button
    private val trinity = LatLng(53.343792, -6.254572)

    private val db = Firebase.firestore
    private lateinit var mAuth : FirebaseAuth

    // Initialize fragments
    private val homeFragment: HomeFragment = HomeFragment()
    private val historyFragment: HistoryFragment = HistoryFragment()
    private val currentUserItinerariesFragment: CurrentUserItinerariesFragment = CurrentUserItinerariesFragment()
    private val globalItinerariesFragment: GlobalItinerariesFragment = GlobalItinerariesFragment()
    private val contactsFragment: ContactsFragment = ContactsFragment()

    companion object {
        val currentUserItineraryList = ArrayList<Itinerary>();
        val globalItineraryList = ArrayList<Itinerary>();
        val userRateList = ArrayList<UserRate>()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        bottomNavigationView = findViewById(R.id.bottom_navigation_bar)
        mapButton = findViewById(R.id.mapButton)

        setupBottomNavigationBarLogic()

        //Detect when map button is pressed and follow intent to map
        mapButton.setOnClickListener{
            val intent = Intent(this@HomeActivity, MapsActivity::class.java)
            startActivity(intent)
        }

        fetchCurrentUserItineraries()
        fetchGlobalItineraries()
        fetchUserRate()
    }

    /**
     * Setup the logic of the fragment view change when we select an item on the navigation bar
     */
    fun setupBottomNavigationBarLogic() {
        // Set default fragment view in the frame layout
        supportFragmentManager.beginTransaction().replace(R.id.main_container, homeFragment)
            .commit()
        bottomNavigationView.selectedItemId = R.id.nav_home

        bottomNavigationView.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            var fragment: Fragment? = null
            when (item.itemId) {
                R.id.nav_home -> fragment = homeFragment
                R.id.nav_map -> fragment = historyFragment
                R.id.nav_add -> fragment = contactsFragment
                R.id.nav_rating -> fragment = globalItinerariesFragment
                R.id.nav_profile -> fragment = currentUserItinerariesFragment
            }
            if (fragment == null) return@OnItemSelectedListener false
            supportFragmentManager.beginTransaction().replace(R.id.main_container, fragment)
                .commit()
            true
        })
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

    // get all rating of the current user
    private fun fetchUserRate() {
        db.collection("user/${mAuth.uid}/itineraryRates")
            .get()
            .addOnSuccessListener { userRateDocuments ->
                for (doc in userRateDocuments) {
                    userRateList.add(UserRate(doc.id, (doc.data.get("rate") as Double).toFloat()))
                }
            }
    }

    private fun storeFetchedItinerariesInList(list: ArrayList<Itinerary>, querySnapshot: QuerySnapshot) {
        for (itineraryDocument in querySnapshot) {
            // Assure that both variables are Double due to firestore issue
            var rating: Double
            var numberOfRates: Double

            val tmpRating = itineraryDocument.data.getOrDefault("rating", 0.0)
            val tmpNumberOfRates = itineraryDocument.data.getOrDefault("number_of_rates", 0.0)

            if (tmpRating is Long)
                rating = tmpRating.toDouble()
            else
                rating = tmpRating as Double

            if (tmpNumberOfRates is Long)
                numberOfRates = tmpNumberOfRates.toDouble()
            else
                numberOfRates = tmpNumberOfRates as Double

            val itinerary = Itinerary(
                itineraryDocument.data.get("title") as String,
                itineraryDocument.id,
                rating.toFloat(),
                numberOfRates.toInt()
            )

            db.collection("itineraries/${itineraryDocument.id}/steps")
                .get()
                .addOnSuccessListener { steps ->
                    for (stepDocument in steps) {
                        val step = Step(
                            stepDocument.id,
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
        tempString = intent.getStringExtra("email")
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