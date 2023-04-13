package com.example.mapstemplate.ui.home

import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapstemplate.*
import com.example.mapstemplate.R
import com.example.mapstemplate.databinding.FragmentHomeBinding
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import okhttp3.*
import java.io.IOException


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var nearbyLocations : ArrayList<String>
    private lateinit var nearbyRatings : ArrayList<Number>
    private lateinit var nearbyCordinates : ArrayList<LatLng>
    private lateinit var nearbyVicinity : ArrayList<String>
    private val trinity = LatLng(53.343792, -6.254572)

    private lateinit var notifications : ArrayList<String>

    private val thisName = "Home Fragment"

    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth : FirebaseAuth

    // Declaration of recycler view variable and recycler view adapter for recommendations and
    // notifications inside the home fragment.
    private lateinit var recommendationsRecycler : RecyclerView
    private lateinit var recommendationsAdapter : RecommendationsAdapter

    private lateinit var notificationsRecycler : RecyclerView
    private lateinit var notificationsAdapter : NotificationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        nearbyLocations = ArrayList()
        nearbyRatings = ArrayList()
        nearbyVicinity = ArrayList()
        nearbyImages = ArrayList()
        nearbyCordinates = ArrayList()
        notifications = ArrayList()

        mDbRef = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()



        //sendNotification("testing")

        //declaration of view model variable and assignment to viewmodel
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        Log.i("MyTag", "creating view for $thisName")

        //set root variable to fragment view
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        //nearbyLocations.add("Test string")

        nearbyLocations.forEach {
            Log.i("PlacesAPI", "Locations list currently contains $it")
            if (nearbyLocations.size == 0) {
                Log.i("PlacesAPI", "No locations available")
            }
        }

        notifications.add("There was a car crash near College Green")
        notifications.add("There is a robbery in progress in the Lloyd Institute")
        notifications.add("A masked gunman is on a mugging spree in the Arts block")
        notifications.add("There are wanted fugitives hiding in the Business Building")
        notifications.add("There was a toxic gas leak in LG12")
        notifications.add("A fire alarm has gone off in Trinity Gym")
        notifications.add("The pavement is cracked underneath the campanile causing increased danger of stubbed toes")
        //notificationsAdapter.notifyDataSetChanged()

        // assign adapter class constructor to recommendationsAdapter, then make the recommendations
        // point to the correct recycler view in the correct LinearLayoutManager, finally attach the
        // recycler view to the adapter
        nearbyPlaces(trinity, 1000, "restaurant")
        recommendationsAdapter = RecommendationsAdapter(nearbyLocations, nearbyRatings,
            nearbyImages, nearbyVicinity, nearbyCordinates
        )
        recommendationsRecycler = root.findViewById(R.id.LocationsRecycler)
        recommendationsRecycler.layoutManager = LinearLayoutManager(context)
        recommendationsRecycler.adapter = recommendationsAdapter

        //The same process as above
        //notificationsAdapter = NotificationsAdapter(notifications)
        //notificationsRecycler = root.findViewById(R.id.NotificationsRecycler)
        //notificationsRecycler.layoutManager = LinearLayoutManager(context)
        //notificationsRecycler.adapter = notificationsAdapter

        //Attach variable to correct textView
        val locationView: TextView = binding.BasedOnLocation
        homeViewModel.text.observe(viewLifecycleOwner) {
            locationView.text = "Recommended based on location"
        }

        //Attach variable to correct textView
        //val notificationsView: TextView = binding.Notifications
        //homeViewModel.text.observe(viewLifecycleOwner) {
        //    notificationsView.text = "Recent Notifications"
        //}



        mDbRef.child("notifications").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(postSnapshot in snapshot.children){
                    // Problem for future Patrick

                    //val notification = snapshot.getValue<String>()
                    //notifications.add("test")
                }
                notificationsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
//        setupNewButtons()

        return root
    }
    private fun replaceActivity(activity: Activity) {
        val intent = Intent(context, activity::class.java)
        startActivity(intent)
    }
//    private fun setupNewButtons() {
//        binding.bottomNavView.setOnItemReselectedListener {
//            when(it.itemId) {
//                R.id.test_home -> replaceActivity(HomeActivity())
//                R.id.test_map -> replaceActivity(MapsActivity())
//                R.id.test_add -> replaceActivity(AddItineraryActivity())
//                R.id.test_rating -> replaceActivity(LikesPage())
//                R.id.test_profile -> replaceActivity(profile())
//                else -> {
//                }
//            }
//            true
//        }
//    }

    private var nearbyImages = ArrayList<Bitmap>()

    private fun parseJson(jsonString: String) {
        val jsonObject = JSONObject(jsonString)
        val resultsArray: JSONArray = jsonObject.getJSONArray("results")

        for (i in 0 until resultsArray.length()) {
            val resultObject = resultsArray.getJSONObject(i)
            val name = resultObject.getString("name")
            val rating = resultObject.getDouble("rating")
            val vicinity = resultObject.getString("vicinity")
            val lat = resultObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat")
            val lng = resultObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng")

            Log.i("PlacesAPI", "====================================================")
            nearbyLocations.add(name)
            nearbyRatings.add(rating)
            nearbyVicinity.add(vicinity)
            nearbyCordinates.add(LatLng(lat,lng))
            Log.i("PlacesAPI", "Name: $name")
            Log.i("PlacesAPI", "Rating: $rating")
            Log.i("PlacesAPI", "Vicinity: $vicinity")
            Log.i("PlacesAPI", "Latitude: $lat")
            Log.i("PlacesAPI", "Longitude: $lng")
        }


        for (i in 0 until resultsArray.length()) {
            val resultObject = resultsArray.getJSONObject(i)
            if (resultObject.has("photos")) {
                val photoArray = resultObject.getJSONArray("photos")
                if (photoArray.length() > 0) {
                    fetchImage(photoArray)
                }
            }
        }
    }


    private fun fetchImage(photoArray: JSONArray) {
        val photoObject = photoArray.getJSONObject(0)
        val photoReference = photoObject.getString("photo_reference")
        val photoWidth = 300 // the desired width of the photo
        val apiKey = "AIzaSyBn1QAii8KpmxExEE2WoN_89XMGhEhfx9Q"

        val photoRequestUrl = "https://maps.googleapis.com/maps/api/place/photo" +
                "?maxwidth=$photoWidth" +
                "&photoreference=$photoReference" +
                "&key=$apiKey"

        object : AsyncTask<Void, Void, Bitmap?>() {
            override fun doInBackground(vararg params: Void?): Bitmap? {
                val httpClient = OkHttpClient()
                val request = Request.Builder().url(photoRequestUrl).build()

                try {
                    val response = httpClient.newCall(request).execute()
                    if (!response.isSuccessful) {
                        // handle unsuccessful response
                        return null
                    }
                    val responseBody = response.body()
                    val photoBytes = responseBody?.bytes()
                    responseBody?.close() // Close the response body to avoid resource leak
                    if (photoBytes != null) {
                        return BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.size)
                    }
                } catch (e: IOException) {
                    // handle IO exception
                }

                return null
            }

            override fun onPostExecute(bitmap: Bitmap?) {
                if (bitmap != null) {
                    // Add the Bitmap to the imageArray
                    Log.i("PlacesAPI", "Was able to add image")
                    this@HomeFragment.nearbyImages.add(bitmap)
                }
            }
        }.execute()
    }




    fun nearbyPlaces(cords: LatLng, radius: Number, type : String){
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

    private fun sendNotification(message : String){
        mDbRef.child("notifications").setValue(message)
        notifications.add(message)
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
    override fun onDestroy(){
        super.onDestroy()
        Log.i("MyTag", "destroying $thisName")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("MyTag", "destroying view for $thisName")
        _binding = null
    }
}