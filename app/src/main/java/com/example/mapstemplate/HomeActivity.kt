package com.example.mapstemplate

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.ArraySet
import android.util.Log
import android.view.Menu
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.mapstemplate.databinding.ActivityHomeBinding
import com.example.mapstemplate.itineraries.UserRate
import com.example.mapstemplate.ui.add_itinerary.AddItineraryFragment
import com.example.mapstemplate.ui.contacts.ContactsFragment
import com.example.mapstemplate.ui.current_user_itineraries.CurrentUserItinerariesFragment
import com.example.mapstemplate.ui.global_itineraries.GlobalItinerariesFragment
import com.example.mapstemplate.ui.home.HomeFragment
import com.example.travelapp.itineraries.Itinerary
import com.example.travelapp.itineraries.Step
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
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
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference


    // Initialize fragments
    private val homeFragment: HomeFragment = HomeFragment()
    private val addItineraryFragment: AddItineraryFragment = AddItineraryFragment()
    private val currentUserItinerariesFragment: CurrentUserItinerariesFragment = CurrentUserItinerariesFragment()
    private val globalItinerariesFragment: GlobalItinerariesFragment = GlobalItinerariesFragment()
    private val contactsFragment: ContactsFragment = ContactsFragment()

    companion object {
        val currentUserItineraryList = ArrayList<Itinerary>();
        val globalItineraryList = ArrayList<Itinerary>();
        val userRateList = ArrayList<UserRate>()
        val mainImageItineraryMap = HashMap<String, File>()
        val userLikeList = ArraySet<String>()
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
        fetchItineraryMainImage()
        fetchUserLikes()
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
                R.id.nav_map -> fragment = globalItinerariesFragment
                R.id.nav_add -> fragment = addItineraryFragment
                R.id.nav_rating -> fragment = contactsFragment
                R.id.nav_profile -> fragment = currentUserItinerariesFragment
            }
            if (fragment == null) return@OnItemSelectedListener false
            supportFragmentManager.beginTransaction().replace(R.id.main_container, fragment)
                .commit()
            true
        })
    }

    /**
     * Fetch the current user likes
     */
    fun fetchCurrentUserItineraries() {
        // clear previous data to prevent bug
        userLikeList.clear()

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

    // get current user itineraries from firestore
    fun fetchUserLikes() {
        // clear previous data to prevent bug
        currentUserItineraryList.clear()

        val collectionLikeRef = db.collection("user/${mAuth.uid}/itineraryLikes")
        collectionLikeRef.get()
            .addOnSuccessListener { likes ->
                for (doc in likes) {
                    try {
                        userLikeList.add(doc.data.get("itinerary_id") as String)
                    } catch (e: Exception) {
                        Log.d("DEBUG", "Dublicate like itinerary in Firestore")
                    }
                }
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

    /**
     * Fetch the main image of an itinerary if existing
     */
    fun fetchItineraryMainImage() {
        // clear previous data to prevent bug
        mainImageItineraryMap.clear()

        Log.d("DEBUG", "START")
        val documentsRef = storageRef.root.child("images_itineraries")
        documentsRef.listAll().addOnSuccessListener {
            it.prefixes.forEach { prefix ->

                prefix.listAll().addOnSuccessListener {
                    it.items.forEach {item ->
                        // Create temp image file
                        val localfile = File.createTempFile("${prefix.name}_${item.name}", ".jpg")
                        item.getFile(localfile).addOnSuccessListener {
                            mainImageItineraryMap.put(prefix.name, localfile)
                            Log.d("DEBUG", "Add main image in list : ${item.path}")
                        }
                    }
                }

            }
        }
    }

    private fun storeFetchedItinerariesInList(list: ArrayList<Itinerary>, querySnapshot: QuerySnapshot) {
        for (itineraryDocument in querySnapshot) {
            // Assure that both variables are Double due to firestore issue
            var rating: Double
            var numberOfRates: Double

            rating = 0.0
            numberOfRates = 0.0

            val itinerary = Itinerary(
                itineraryDocument.data.get("title") as String,
                itineraryDocument.id,
                rating.toFloat(),
                numberOfRates.toInt()
            )

            // try to get the main image of each itinerary
            //fetchItineraryMainImage(itinerary)

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